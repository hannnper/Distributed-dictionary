// Dictionary client main class

package dictionary;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import java.awt.EventQueue;

public class DictionaryClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DictionaryClient.jar <host> <port>");
            return;
        }
        // get the host and port from the command line
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // Start the GUI
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientGui window = new ClientGui(host, port);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
