package dictionary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class DictionaryClient {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the dictionary client!");

        // TODO: get the host and port from the command line
        String host = "localhost";
        int port = 9015;

        try (Socket socket = new Socket(host, port);) {
            System.out.println("Connected to server on port " + socket.getPort());

            // Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		    String sendData ="Hello server!";

            output.writeUTF(sendData);
            System.out.println("Sent: " + sendData);
            output.flush();

            boolean flag = true;
		    while(flag)
		    {
		    	if(input.available()>0) {
		    		String message = input.readUTF();
		    		System.out.println(message);
		    		flag = false;
		    	}
		    }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
}
