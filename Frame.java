package serverFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * Frame.class is used to represent the server environment in a user friendly GUI.
 * 
 * @author Wahab Meskinyar 
 * @see javax.Swing Library
 * @param args
 * @since 06.07.2021
 */

public class Frame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextArea keyboard;

	private JButton SendButton;
	private JButton startUpButton;

	private JLabel serverState;

	private JTextArea InsightArea;

	private Server server;
	private JTextField textField;

	private int port;

	private String time;

	/**
	 * launches the Application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Frame().setVisible(true);
		});
	}
	
	/**
	 * Terminates the App.
	 * If the server is online, the connection will cut off resulting an App closure.
	 * All opened resources including threads, Scanners, Readers, StreamWriters will close simultaneously.
	 * @see Server.class
	 */
	@Override
	public void dispose() {
		if (server.isRunning()) {
			try {
				server.turnOff();
				server.CloseServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.dispose();
		System.exit(0);
	}

	/**
	 * Initializing attributes
	 * 
	 */
	public Frame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 449, 363);
		this.setResizable(false);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 433, 324);
		panel.setLayout(null);
		getContentPane().add(panel);

		startUpButton = new JButton("Run");
		startUpButton.addActionListener(this);
		startUpButton.setActionCommand("Run");
		startUpButton.setBounds(132, 11, 107, 23);
		panel.add(startUpButton);

		InsightArea = new JTextArea();
		InsightArea.setLineWrap(true);
		InsightArea.setEditable(false);

		JScrollPane scroller = new JScrollPane(InsightArea);
		scroller.setBounds(37, 70, 366, 100);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panel.add(scroller);

		keyboard = new JTextArea();
		keyboard.setLineWrap(true);

		JScrollPane scroller1 = new JScrollPane(keyboard);
		scroller1.setBounds(38, 192, 365, 85);
		scroller1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroller1);

		JLabel ServerStateLabel = new JLabel("Server State");
		ServerStateLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		ServerStateLabel.setBounds(272, 13, 89, 23);
		panel.add(ServerStateLabel);

		serverState = new JLabel("Offline");
		serverState.setFont(new Font("Arial", Font.PLAIN, 14));
		serverState.setForeground(Color.red);
		serverState.setBounds(363, 12, 70, 19);
		panel.add(serverState);

		SendButton = new JButton("Send");
		SendButton.addActionListener(this);
		SendButton.setEnabled(false);
		SendButton.setActionCommand("Send");
		SendButton.setBounds(314, 278, 89, 23);
		panel.add(SendButton);

		JLabel lblNewLabel = new JLabel("Server-Insight");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel.setBounds(38, 45, 89, 23);
		panel.add(lblNewLabel);

		JLabel Inputlabel = new JLabel("Output To Clients");
		Inputlabel.setBounds(38, 177, 89, 14);
		panel.add(Inputlabel);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				//@formatter:off
				textField.setEditable((Character.isDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) ? true : false);
				//@formatter:on
			}
		});
		textField.setBounds(63, 12, 59, 20);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 14));
		lblNewLabel_1.setBounds(16, 12, 37, 19);
		panel.add(lblNewLabel_1);

		server = new Server(InsightArea);
	}

	/**
	 * Invoked when an action occurs. E.g. if a button is triggered, 
	 * a specific code is executed depending on its ActionCommand.
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Send":
			if (server.isRunning()) {
				time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

				server.printOutToAll(String.format("[server] %s: %s", time, keyboard.getText()), "", false);

				//@formatter:off
				InsightArea.setText(InsightArea.getText() + String.format("\n[server] %s: %s", time, keyboard.getText()));
				//@formatter:on

				keyboard.setText("");
			}
			break;
		case "Run":
			//@formatter:off
			if (textField.getText().isBlank() || textField.getText().replaceAll("[^0-9]", "").length() == 0	|| Integer.parseInt(textField.getText()) > 65535) {
			//@formatter:on
				JOptionPane.showMessageDialog(this, "please Enter a valid Port");
				break;
			} else {
				try {
					if (port == Integer.parseInt(textField.getText())) {
						JOptionPane.showMessageDialog(this, "please select a different port");
					} else {
						this.port = Integer.parseInt(textField.getText());
						startUpButton.setText("Shutdown");
						startUpButton.setActionCommand("Shutdown");

						SendButton.setEnabled(true);
						textField.setEditable(false);
						
						server.setServerState(true);
						server.start(this.port);

						serverState.setForeground(Color.green);
						serverState.setText("Online!");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			}
		case "Shutdown":
			serverState.setForeground(Color.red);
			serverState.setText("Offline!");

			startUpButton.setText("Run");
			startUpButton.setActionCommand("Run");

			SendButton.setEnabled(false);

			textField.setEditable(true);

			server.turnOff();
		}
	}
}
