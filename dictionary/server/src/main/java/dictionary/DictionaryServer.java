// Dictionary server main class

package dictionary;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.net.ServerSocket;
import java.awt.EventQueue;
import com.formdev.flatlaf.themes.FlatMacLightLaf;


public class DictionaryServer {
    public static ThreadPoolExecutor threadPool = null;
    public static ServerSocket listener = null;
    public static void main(String[] args) {

        // start the GUI
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGui window = new ServerGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    System.out.println("Error starting server GUI");
                    e.printStackTrace();
                }
            }
        });

    }

    public static ThreadPoolExecutor createThreadPool() {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5, // core thread pool size (always running threads)
                20, // maximum thread pool size
                60L, // time for idle threads to be kept alive
                TimeUnit.SECONDS, // time unit for the keep alive time
                new LinkedBlockingQueue<Runnable>() // work queue
            );
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return threadPool;
    }

    public static void startServer(int port) throws Exception {

        // Create a server socket
        listener = new ServerSocket(port);
        
        // Create a thread pool to handle incoming requests
        threadPool = createThreadPool();

    }

    public static void stopServer() throws Exception {
        listener.close();
        threadPool.shutdown();
    }

    public static void listenForClients() throws Exception {
        while (true) {
            Socket clientSocket = listener.accept();

            // Create a new worker runnable and submit it to the executor
            WorkerRunnable worker = new WorkerRunnable(clientSocket);
            threadPool.execute(worker);
        }
    }
}
