// Dictionary client connection class

package dictionary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientConnect {

    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public ClientConnect(String host, int port) throws Exception{

        // connect to the server
        socket = new Socket(host, port);

        // set input and output streams
        input = new DataInputStream(socket.getInputStream());
        
        output = new DataOutputStream(socket.getOutputStream());

    }

    public void close() throws Exception{

        input.close();
        output.close();
        socket.close();
    }
}
