// Dictionary Server worker runnable class

package dictionary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
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

            // Log the connection and message to the server GUI
            String guiMsg = ("Accepted connection from " + ipSock + "\n" 
                                + "Received: " + strMessage + "\n");
            ServerGui.addLog(guiMsg);
            
            Message message = Message.fromJson(strMessage);
            Message response = null;

            // Log the ip and command of the client to the log file
            logger.info("IP: {}, command: {}, word: {}", ipSock, message.getCommand(), message.getWord());

            // check message for errors
            if (!message.isValid()) {
                // errors are passed back in the response
                response = message.makeResponse();
                response.setSuccess(false);
            }
            // Handle the command
            else if (message.getCommand().equals(Message.QUERY)) {
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
            else if (message.getCommand().equals(Message.EDIT_MEANING)) {
                response = handleEdit(message);
            }
            else {
                response = handleInvalidCommand(message);
            }

            // Send the response back to the client
            String strResponse = response.toJson();
            ServerGui.addLog("Sending: " + strResponse + "\n");
            output.writeUTF(strResponse);
            
            // Close the streams
            output.close();
            input.close();

        } catch (IOException e) {
            ServerGui.addLog("Error: " + e + "\n");
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

    private synchronized Message handleAdd(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        String error = response.getError();
        if (error != null && error.equals(Message.ERROR_WORD_NOT_FOUND_MSG)) {
            // word not in database so safe to add
            // reset error before attempting to add new word-meaning pair
            response.setError(null);
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
            response.setSuccess(false);;
        }
        return response;
    }

    private synchronized Message handleUpdate(Message message) {
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

    private synchronized Message handleRemoveWord(Message message) {
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

    private synchronized Message handleRemoveMeaning(Message message) {
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

    private synchronized Message handleEdit(Message message) {
        MySqlHandler db = new MySqlHandler();
        Message response = message.makeResponse();

        // check if word is in database first
        db.lookupWord(message, response);
        if (!response.getSuccess()) {
            // word is not in database or another error occurred
            return response;
        }

        // check if old meaning is in meanings
        String oldMeaning = message.getOldMeaning();
        if (!response.getMeanings().contains(oldMeaning)) {
            // old meaning is not in meanings
            response.setError(Message.ERROR_MEANING_NOT_FOUND_MSG);
            response.setSuccess(false);
            return response;
        }

        // check if new meaning is in meanings
        String newMeaning = message.getMeaning();
        if (response.getMeanings().contains(newMeaning)) {
            // new meaning already exists for this word
            response.setError(Message.ERROR_MEANING_EXISTS_MSG);
            response.setSuccess(false);
            return response;
        }

        // now update this meaning for this word
        db.editMeaning(message, response);

        return response;
    }
}
