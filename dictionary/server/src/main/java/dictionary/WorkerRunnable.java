package dictionary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

    private static final Logger logger = LogManager.getLogger(WorkerRunnable.class);

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // Get the input/output streams
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            
            // Process the input
            String strMessage = input.readUTF();
            SocketAddress ipSock = clientSocket.getRemoteSocketAddress();
            System.out.println("Accepted connection from " + ipSock);
            System.out.println("Received: " + strMessage);
            Message message = Message.fromJson(strMessage);
            Message response = null;
            System.out.println("Command: " + message.getCommand());

            // Log the ip and command of the client
            // TODO: add username? other info?
            logger.info("IP: {}, command: {}, word: {}", ipSock, message.getCommand(), message.getWord());

            // check message for errors
            if (!message.isValid()) {
                // errors are passed back in the response
                response = message.makeResponse();
                response.setSuccess(false);
                return;
            }
            // Handle the command
            if (message.getCommand().equals(Message.QUERY)) {
                response = handleQuery(message);
            }
            else if (message.getCommand().equals(Message.ADD)) {
                response = handleAdd(message);
            }
            else if (message.getCommand().equals(Message.UPDATE)) {
                response = handleUpdate(message);
            }
            else if (message.getCommand().equals(Message.REMOVE_WORD)) {
                response = handleRemoveWord(message);
            }
            else if (message.getCommand().equals(Message.REMOVE_MEANING)) {
                response = handleRemoveMeaning(message);
            }
            else {
                response = handleInvalidCommand(message);
            }

            // Send the response back to the client
            String strResponse = response.toJson();
            System.out.println("Sending: " + strResponse);
            output.writeUTF(strResponse);
            
            // Close the streams
            output.close();
            input.close();

        } catch (IOException e) {
            // TODO: Handle exception
            System.out.println("Error: " + e);
        }
    }

    private Message handleInvalidCommand(Message message) {
        Message response = message.makeResponse();
        response.setError(Message.ERROR_INVALID_COMMAND_MSG);
        response.setSuccess(false);
        return response;
    }

    private Message handleQuery(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // lookup word in the dictionary database
        db.lookupWord(message, response);

        return response;
    }

    private Message handleAdd(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        if (response.getError().equals(Message.ERROR_WORD_NOT_FOUND_MSG)) {
            // word not in database so safe to add
            // reset error before attempting to add new word-meaning pair
            response.setError("");
            // add word to the dictionary database
            db.addWord(message, response);
        }
        else if (response.getSuccess()) {
            // set appropriate error message
            response.setError(Message.ERROR_WORD_EXISTS_MSG);
            response.setSuccess(false);
        }
        else {
            // this occurs if there was some other failure (e.g. database)
            // leave the existing error message
            ;
        }
        return response;
    }

    private Message handleUpdate(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        // check if new meaning already in meanings
        String newMeaning = message.getMeaning();
        if (response.getSuccess() && response.getMeanings().contains(newMeaning)) {
            // this meaning already exists for this word
            response.setError(Message.ERROR_MEANING_EXISTS_MSG);
            response.setSuccess(false);
        }
        else if (response.getSuccess()) {
            // if the word is already in the database, add another meaning
            db.addWord(message, response);
        }

        return response;
    }

    private Message handleRemoveWord(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        if (!response.getSuccess()) {
            // word is not in database or another error occurred
            return response;
        }

        // now delete all meanings for this word
        db.removeWord(message, response);

        return response;
    }

    private Message handleRemoveMeaning(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        if (!response.getSuccess()) {
            // word is not in database or another error occurred
            return response;
        }

        // check if meaning is in meanings
        String meaning = message.getMeaning();
        if (!response.getMeanings().contains(meaning)) {
            // meaning is not in meanings
            response.setError(Message.ERROR_MEANING_NOT_FOUND_MSG);
            response.setSuccess(false);
            return response;
        }

        // now delete this meaning for this word
        db.removeMeaning(message, response);

        return response;
    }
}
