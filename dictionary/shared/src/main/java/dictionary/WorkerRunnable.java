package dictionary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

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
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            System.out.println("Received: " + strMessage);
            Message message = Message.fromJson(strMessage);
            Message response = null;
            System.out.println("Command: " + message.getCommand());
            if (message.getCommand().equals(Message.QUERY)) {
                response = handleQuery(message);
                
            }
            else {
                response = handleInvalidCommand(message);
                System.out.println("ERROR due to client: " + Message.ERROR_INVALID_COMMAND_MSG);
                System.out.println("Client request: " + message.getCommand());
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
        Message response = message;
        response.setError(Message.ERROR_INVALID_COMMAND_MSG);
        return response;
    }

    private Message handleQuery(Message message) {
        String word = message.getWord();
        ArrayList<String> meanings = new ArrayList<String>();

        // TODO: Implement lookup in the dictionary

        Message response = new Message();
        response.setCommand(Message.QUERY_RESPONSE);
        response.setWord(word);
        response.setMeanings(meanings);
        return response;
    }
}
