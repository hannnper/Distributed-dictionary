package dictionary;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTable;

public class ClientGui {

	public JFrame frame;
	public JTextField textField;
	public JTextArea textArea;

    public ClientGui(String host, int port) {
        initialize(host, port);
    }

    private void initialize(String host, int port) {

        frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);


		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.weightx = 0.5;
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		frame.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		

		// query button
		JButton queryButton = new JButton("Query");
		GridBagConstraints gbc_queryButton = new GridBagConstraints();
		gbc_queryButton.insets = new Insets(0, 0, 5, 0);
		gbc_queryButton.gridx = 2;
		gbc_queryButton.gridy = 0;
		frame.getContentPane().add(queryButton, gbc_queryButton);

		// add button
		JButton addButton = new JButton("Add");
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(0, 0, 5, 0);
		gbc_addButton.gridx = 3;
		gbc_addButton.gridy = 0;
		frame.getContentPane().add(addButton, gbc_addButton);

		// update meaning button
		JButton updateButton = new JButton("Update Meaning");
		GridBagConstraints gbc_updateButton = new GridBagConstraints();
		gbc_updateButton.insets = new Insets(0, 0, 5, 0);
		gbc_updateButton.gridx = 0;
		gbc_updateButton.gridy = 2;
		frame.getContentPane().add(updateButton, gbc_updateButton);

		// remove word button
		JButton removeWordButton = new JButton("Remove Word");
		GridBagConstraints gbc_removeWordButton = new GridBagConstraints();
		gbc_removeWordButton.insets = new Insets(0, 0, 5, 0);
		gbc_removeWordButton.gridx = 1;
		gbc_removeWordButton.gridy = 2;
		frame.getContentPane().add(removeWordButton, gbc_removeWordButton);


		//JButton removeMeaningButton = new JButton("Remove Meaning");

		
		textArea = new JTextArea();
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.insets = new Insets(0, 0, 0, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		gbc_textArea.gridwidth = 4;
		frame.getContentPane().add(textArea, gbc_textArea);
		
		// pressing this button submits a query
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();

				// Connect to the server
				ClientConnect connection = new ClientConnect(host, port);

				// Send query
				try {
					Message message = Message.makeQuery(word);
					String sendData = message.toJson();
					connection.output.writeUTF(sendData);
					connection.output.flush();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to query this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("Error sending query");
					System.out.println("Error: " + e);
				}

				// Receive response and display
				try {
					String strMessage = connection.input.readUTF();
					System.out.println("Received: " + strMessage);
					Message response = Message.fromJson(strMessage);
					if (response.getSuccess()) {
						displayMeanings(word, response.getMeanings());
					}
					else {
						JOptionPane.showMessageDialog(null, response.getError(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} 
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to query this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("Error: " + e);
				}
				
				// Close the connection
				connection.close();
			}
		});

		// pressing this button brings up a window to add a word
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();
				// popup window to add a word
				String response = JOptionPane.showInputDialog(null, "Enter meaning for \"" + word + "\":", "Input", JOptionPane.QUESTION_MESSAGE);
				if (response != null) {
					// User provided input
					Message message = Message.makeAdd(word, response);
					
					// Connect to the server
					ClientConnect connection = new ClientConnect(host, port);

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to add this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}

					// receive response
					try {
						String strMessage = connection.input.readUTF();
						Message responseMessage = Message.fromJson(strMessage);
						if (responseMessage.getSuccess()) {
							JOptionPane.showMessageDialog(null, "Word added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
							displayMeanings(word, responseMessage.getMeanings());
						}
						else {
							JOptionPane.showMessageDialog(null, responseMessage.getError(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to add this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		// pressing this button brings up a window to update a meaning
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();
				// popup window to add the new meaning
				String response = JOptionPane.showInputDialog(null, "Enter new meaning for \"" + word + "\":", "Input", JOptionPane.QUESTION_MESSAGE);
				if (response != null) {
					// User provided input
					Message message = Message.makeUpdate(word, response);
					
					// Connect to the server
					ClientConnect connection = new ClientConnect(host, port);

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to update this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}

					// receive response
					try {
						String strMessage = connection.input.readUTF();
						Message responseMessage = Message.fromJson(strMessage);
						if (responseMessage.getSuccess()) {
							JOptionPane.showMessageDialog(null, "Meaning updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
							displayMeanings(word, responseMessage.getMeanings());
						}
						else {
							JOptionPane.showMessageDialog(null, responseMessage.getError(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to update this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		// pressing this button removes a word
		removeWordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();
				// popup window to confirm deletion
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove \"" + word + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					// User confirmed deletion
					Message message = Message.makeRemoveWord(word);
					
					// Connect to the server
					ClientConnect connection = new ClientConnect(host, port);

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to remove this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}

					// receive response
					try {
						String strMessage = connection.input.readUTF();
						Message responseMessage = Message.fromJson(strMessage);
						if (responseMessage.getSuccess()) {
							JOptionPane.showMessageDialog(null, "Word removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
							displayMeanings(word, new ArrayList<String>());
						}
						else {
							JOptionPane.showMessageDialog(null, responseMessage.getError(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to remove this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

	}

	public void displayMeanings(String word, ArrayList<String> meanings) {
		textArea.setText("");
		int i = 1;
		for (String meaning : meanings) {
			textArea.append(i + ":\n    " + meaning + "\n");
			i += 1;
		}
	}
}
