// Dictionary Server GUI

package dictionary;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerGui {

	public JFrame frame;
	public static JTextArea textArea;
    public JButton startButton;
    public JButton stopButton;

    public ServerGui() {
        initialize();
    }

    private void initialize(){
        frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

        // text area for server information
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(10);
        textArea.setColumns(50);
        textArea.setAutoscrolls(true);
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.insets = new Insets(0, 0, 0, 0);
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.gridx = 0;
        gbc_textArea.gridy = 0;

        // scrollpane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        frame.getContentPane().add(scrollPane, gbc_scrollPane);

        // panel for buttons
        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 0, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        frame.getContentPane().add(panel, gbc_panel);

        // start server button
        startButton = new JButton("Start");
        GridBagConstraints gbc_startButton = new GridBagConstraints();
        gbc_startButton.insets = new Insets(5, 5, 5, 5);
        gbc_startButton.gridx = 0;
        gbc_startButton.gridy = 0;
        gbc_startButton.anchor = GridBagConstraints.WEST;
        startButton.setEnabled(true);
        panel.add(startButton, gbc_startButton);

        // start button will start the server
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // ask user for the port number
                String port = JOptionPane.showInputDialog(frame, "Enter the port number:", "Port Number", JOptionPane.QUESTION_MESSAGE);
                try {
                    Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    textArea.append("Invalid port number!\n");
                    return;
                }
                textArea.append("Server is starting!\n");
                try {
                    DictionaryServer.startServer(Integer.parseInt(port));
                } catch (Exception e) {
                    textArea.append("Error starting server!\n");
                    textArea.append(e.getMessage());
                    return;
                }
                textArea.append("Server is running on port " + port + "\n");
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

                // in a new thread, listen for client connections
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            DictionaryServer.listenForClients();
                        } catch (Exception e) {
                            textArea.append("Error listening for clients!\n");
                            textArea.append(e.getMessage());
                        }
                    }
                });
                thread.start();
            }
        });

        // stop server button
        stopButton = new JButton("Stop");
        GridBagConstraints gbc_stopButton = new GridBagConstraints();
        gbc_stopButton.insets = new Insets(5, 5, 5, 5);
        gbc_stopButton.gridx = 1;
        gbc_stopButton.gridy = 0;
        gbc_stopButton.anchor = GridBagConstraints.WEST;
        stopButton.setEnabled(false);
        panel.add(stopButton, gbc_stopButton);

        // stop button will stop the server
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                textArea.append("Server is stopping!\n");
                try {
                    DictionaryServer.stopServer();
                } catch (Exception e) {
                    textArea.append("Error stopping server!\n");
                    textArea.append(e.getMessage());
                    return;
                }
                textArea.append("Server has stopped!\n");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
    }

    public static void addLog(String log) {
        // add a log message to the text area in a thread-safe way
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                textArea.append(log + "\n");
            }
        });
    }

}
