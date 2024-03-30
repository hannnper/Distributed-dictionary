import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.net.ServerSocket;

public class DictionaryServer {
    public static void main(String[] args) throws Exception {
        System.out.println("Server is starting!");

        try {
            ServerSocket listener = new ServerSocket(9015);
            System.out.println("Server is listening on port " + listener.getLocalPort());
            
            // Create a thread pool to handle incoming requests
            // TODO: check what errors are thrown and handle them
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, // core thread pool size (always running threads)
                20, // maximum thread pool size
                60L, // time for idle threads to be kept alive
                TimeUnit.SECONDS, // time unit for the keep alive time
                new LinkedBlockingQueue<Runnable>() // work queue
            );
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            }

            executor.shutdown();
            listener.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
