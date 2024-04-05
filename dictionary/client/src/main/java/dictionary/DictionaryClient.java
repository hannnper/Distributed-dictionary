package dictionary;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DictionaryClient extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
        System.out.println("Welcome to the dictionary client!");

        // TODO: get the host and port from the command line
        String host = "localhost";
        int port = 9015;

        try (Socket socket = new Socket(host, port);) {
            System.out.println("Connected to server on port " + socket.getPort());

            // Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            Message msg = new Message();
            msg.setCommand(Message.QUERY);
            msg.setWord("hello");
		    String sendData = msg.toJson();

            output.writeUTF(sendData);
            System.out.println("Sent: " + sendData);
            output.flush();

            boolean flag = true;
            String message = "";
		    while(flag)
		    {
		    	if(input.available()>0) {
		    		message += input.readUTF();
		    		flag = false;
		    	}
		    }
            System.out.println("Received: " + message);

            // Interpret Message
            Message inMsg = Message.fromJson(message);
            System.out.println("Command: " + inMsg.getCommand());
            System.out.println("Word: " + inMsg.getWord());
            System.out.println("Meanings: " + inMsg.getMeanings());

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    @Override
    public void start(Stage primaryStage)  {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
            primaryStage.setTitle("Dictionary Client");
            primaryStage.setScene(new Scene(root, 300, 275));
            primaryStage.show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
