package dictionary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientConnect {

    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public ClientConnect(String host, int port) {

        try {
            socket = new Socket(host, port);
            System.out.println("Connected to server on port " + socket.getPort());

            // set input and output streams
			input = new DataInputStream(socket.getInputStream());
			
		    output = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            System.out.println("Connection error");
            System.out.println("Error: " + e);
        }

    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing connection");
            System.out.println("Error: " + e);
        }
    }
}
