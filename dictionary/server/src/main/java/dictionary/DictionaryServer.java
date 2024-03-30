package dictionary;

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
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5, // core thread pool size (always running threads)
                20, // maximum thread pool size
                60L, // time for idle threads to be kept alive
                TimeUnit.SECONDS, // time unit for the keep alive time
                new LinkedBlockingQueue<Runnable>() // work queue
            );
            threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

                // Create a new worker runnable and submit it to the executor
                WorkerRunnable worker = new WorkerRunnable(clientSocket);
                threadPool.execute(worker);
            }

            // TODO: create and catch exception that can be thrown when admin sends a shutdown command
            // threadPool.shutdown();
            // listener.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
