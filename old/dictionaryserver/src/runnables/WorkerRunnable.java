package runnables;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


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
            
            // Process the input/output here
            String message = input.readUTF();
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            System.out.println("Received: " + message);
            output.writeUTF("Hello client! This is server speaking! 🥰");

            
            // Close the streams
            output.close();
            input.close();

        } catch (IOException e) {
            // TODO: Handle exception
            System.out.println("Error: " + e);
        }
    }
}
