// Dictionary Client GUI

package dictionary;

import java.awt.Component;
import java.awt.FlowLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JList;

public class ClientGui {

	public JFrame frame;
	public JTextField textField;
	public JPanel meaningsPane;
	public JList<String> meaningsList;

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


		// word input
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(5, 5, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.weightx = 1.0;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		frame.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);

		// query button
		JButton queryButton = new JButton("Query");
		GridBagConstraints gbc_queryButton = new GridBagConstraints();
		gbc_queryButton.insets = new Insets(5, 0, 5, 5);
		gbc_queryButton.gridx = 1;
		gbc_queryButton.gridy = 0;
		frame.getContentPane().add(queryButton, gbc_queryButton);

		// add button
		JButton addButton = new JButton("Add");
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(5, 0, 5, 5);
		gbc_addButton.gridx = 2;
		gbc_addButton.gridy = 0;
		frame.getContentPane().add(addButton, gbc_addButton);

		// bottom panel for buttons
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
		gbc_bottomPanel.insets = new Insets(0, 0, 0, 0);
		gbc_bottomPanel.fill = GridBagConstraints.BOTH;
		gbc_bottomPanel.gridx = 0;
		gbc_bottomPanel.gridy = 2;
		gbc_bottomPanel.gridwidth = 3;
		frame.getContentPane().add(bottomPanel, gbc_bottomPanel);

		// update meaning button
		JButton updateButton = new JButton("Add Meaning");
		GridBagConstraints gbc_updateButton = new GridBagConstraints();
		gbc_updateButton.insets = new Insets(0, 5, 5, 0);
		gbc_updateButton.anchor = GridBagConstraints.WEST;
		gbc_updateButton.gridx = 0;
		gbc_updateButton.gridy = 0;
		bottomPanel.add(updateButton, gbc_updateButton);

		// remove meaning button
		JButton removeMeaningButton = new JButton("Remove Meaning");
		GridBagConstraints gbc_removeMeaningButton = new GridBagConstraints();
		gbc_removeMeaningButton.insets = new Insets(0, 5, 5, 0);
		gbc_removeMeaningButton.anchor = GridBagConstraints.WEST;
		gbc_removeMeaningButton.gridx = 1;
		gbc_removeMeaningButton.gridy = 0;
		bottomPanel.add(removeMeaningButton, gbc_removeMeaningButton);

		// edit meaning button
		JButton editMeaningButton = new JButton("Edit Meaning");
		GridBagConstraints gbc_editMeaningButton = new GridBagConstraints();
		gbc_editMeaningButton.insets = new Insets(0, 5, 5, 0);
		gbc_editMeaningButton.anchor = GridBagConstraints.WEST;
		gbc_editMeaningButton.gridx = 2;
		gbc_editMeaningButton.gridy = 0;
		bottomPanel.add(editMeaningButton, gbc_editMeaningButton);

		// remove word button
		JButton removeWordButton = new JButton("Remove Word");
		GridBagConstraints gbc_removeWordButton = new GridBagConstraints();
		gbc_removeWordButton.insets = new Insets(0, 5, 5, 0);
		gbc_removeWordButton.anchor = GridBagConstraints.WEST;
		gbc_removeWordButton.gridx = 3;
		gbc_removeWordButton.gridy = 0;
		bottomPanel.add(removeWordButton, gbc_removeWordButton);

		// meanings panel
		JPanel meaningsPane = new JPanel();
		// putting the meanings pane in a scroll pane
		JScrollPane scrollPane = new JScrollPane(meaningsPane);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.gridwidth = 3;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);

		// adding the JList to the meanings pane
		meaningsList = new JList<String>();
		GridBagConstraints gbc_meaningsList = new GridBagConstraints();
		gbc_meaningsList.fill = GridBagConstraints.BOTH;
		gbc_meaningsList.weightx = 1.0;
		gbc_meaningsList.weighty = 1.0;
		gbc_meaningsList.gridx = 0;
		gbc_meaningsList.gridy = 0;
		gbc_meaningsList.anchor = GridBagConstraints.WEST;
		meaningsList.setAlignmentX(Component.LEFT_ALIGNMENT);
		meaningsPane.add(meaningsList, gbc_meaningsList);
		
		// pressing this button submits a query
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();

				// Connect to the server
				ClientConnect connection = null;
				try {
					connection = new ClientConnect(host, port);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

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
					return;
				}

				// Receive response and display
				try {
					String strMessage = connection.input.readUTF();
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
					return;
				}
				
				// Close the connection
				try {
					connection.close();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
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
					ClientConnect connection = null;
					try {
						connection = new ClientConnect(host, port);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to add this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
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
						return;
					}
					// Close the connection
					try {
						connection.close();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
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
					ClientConnect connection = null;
					try {
						connection = new ClientConnect(host, port);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to update this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
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
						return;
					}
					// Close the connection
					try {
						connection.close();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
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
					ClientConnect connection = null;
					try {
						connection = new ClientConnect(host, port);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to remove this word. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
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
						return;
					}
					// Close the connection
					try {
						connection.close();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

		// pressing this button removes a meaning
		removeMeaningButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();
				// popup window to confirm deletion
				String meaning = meaningsList.getSelectedValue();
				if (meaning == null) {
					JOptionPane.showMessageDialog(null, "Please select a meaning to remove", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the meaning \"" + meaning + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					// User confirmed deletion
					Message message = Message.makeRemoveMeaning(word, meaning);
					
					// Connect to the server
					ClientConnect connection = null;
					try {
						connection = new ClientConnect(host, port);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to remove this meaning. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					}

					// receive response
					try {
						String strMessage = connection.input.readUTF();
						Message responseMessage = Message.fromJson(strMessage);
						if (responseMessage.getSuccess()) {
							JOptionPane.showMessageDialog(null, "Meaning removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
							displayMeanings(word, responseMessage.getMeanings());
						}
						else {
							JOptionPane.showMessageDialog(null, responseMessage.getError(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to remove this meaning. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					}
					// Close the connection
					try {
						connection.close();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

		// pressing this button brings up a window to edit a meaning
		editMeaningButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = textField.getText().strip();
				// popup window to edit the meaning
				String meaning = meaningsList.getSelectedValue();
				if (meaning == null) {
					JOptionPane.showMessageDialog(null, "Please select a meaning to edit", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String response = JOptionPane.showInputDialog(null, "Enter new meaning for \"" + word + "\":", "Input", JOptionPane.QUESTION_MESSAGE, null, null, meaning).toString();
				if (response != null) {
					
					// Connect to the server
					ClientConnect connection = null;
					try {
						connection = new ClientConnect(host, port);
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to connect to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						Message message = Message.makeEditMeaning(word, meaning, response);
						String sendData = message.toJson();
						connection.output.writeUTF(sendData);
						connection.output.flush();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to edit this meaning. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					}

					// receive response
					try {
						String strMessage = connection.input.readUTF();
						Message responseMessage = Message.fromJson(strMessage);
						if (responseMessage.getSuccess()) {
							JOptionPane.showMessageDialog(null, "Meaning edited successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
							displayMeanings(word, responseMessage.getMeanings());
						}
						else {
							JOptionPane.showMessageDialog(null, responseMessage.getError(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to edit this meaning. Please Check the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					}
					// Close the connection
					try {
						connection.close();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to close the connection to dictionary server", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

	}

	public void displayMeanings(String word, ArrayList<String> meanings) {
		meaningsList.setListData(meanings.toArray(new String[0]));
	}
}
