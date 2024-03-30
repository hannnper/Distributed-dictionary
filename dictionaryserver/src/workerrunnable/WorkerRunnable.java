package workerrunnable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // Get the input/output streams
            InputStream inputStream  = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            PrintWriter output = new PrintWriter(outputWriter, true);

            // Process the input/output here
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            System.out.println("Received: " + inputStream.read());
            output.println("Hello client! This is server speaking! 🥰");

            // Close the streams and socket
            outputStream.close();
            inputStream.close();
            clientSocket.close();

        } catch (IOException e) {
            // TODO: Handle exception
            System.out.println("Error: " + e);
        }
    }
}
