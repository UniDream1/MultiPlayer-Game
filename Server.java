package serverFrame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JTextArea;

/**
 * Server bound to a port on the local Network, Waiting for clients to make a connection.
 * Once the client has successfuly connected, then its passed over to ClientHandler.class to  
 * manage events and operation invoked by the client.  
 * 
 * @Alert the words client and User are used interchangeably 
 * for the purpose of keeping things as moderate as possible.
 * 
 * @author Wahab Meskinyar
 * @since 06.03.2021
 * @see ClientHandler.class
 */
public class Server {

	/**
	 * Attributes definition
	 */
	private static final String EXIT_OPERATION = "69E_O";

	private ServerSocket server;

	private ArrayList<ClientHandler> clientsList = new ArrayList<ClientHandler>();

	private boolean isRunning = false;

	private String tempUsersID;

	private JTextArea InsightArea;

	private int port;

	/**
	 * recieves JTextArea as parameter, in order to visualize the ongoing traffic.
	 * 
	 * @param InsightArea
	 */
	public Server(JTextArea InsightArea) {
		this.InsightArea = InsightArea;
	}

	/**
	 * sets JTextArea value to the parameter passed in.
	 * 
	 * @param msg
	 */
	public void setInsightAreaText(String msg) {
		InsightArea.setText(InsightArea.getText() + msg);
	}

	/**
	 * wakes up the server until it it's shut down again.
	 * 
	 * @param port
	 */
	public void start(int port) {
		this.port = port;
		Thread t = new Thread(() -> {
			try {
				server = new ServerSocket(port);

				InsightArea.setText("Server is Online!");

				while (true) {
					Socket client = server.accept();
					if (!isRunning) {
						break;
					}

					//@formatter:off
					String userNotification = "\n ----------------------\n"
											+ "|new User has connected|"
											+ "\n ----------------------";
					//@formatter:on
					InsightArea.setText(InsightArea.getText() + String.format("%s", userNotification));

					ClientHandler handler = new ClientHandler(client, this);
					clientsList.add(handler);
					handler.start();

				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				server = null;
				System.out.println("handlers length = " + clientsList.size());
				System.out.println("thread terminated!");
			}

		});
		t.start();
	}

	/**
	 * returns current serverState, weather it is on- or offline.
	 * 
	 * @return true | false
	 */
	public boolean isRunning() {
		return server != null;
	}

	/**
	 * closes the serverSocket
	 * 
	 * @throws IOException
	 */
	public void CloseServer() throws IOException {
		if (server != null) {
			server.close();
		}
	}

	/**
	 * total shutdown, all resources and Threads are eliminated.
	 */
	public void turnOff() {
		try {
			isRunning = false;
			printOutToAll("Server has shut down due to Maintenance!", "", false);
			Thread.sleep(5);
			Socket dummyClient = new Socket(InetAddress.getLocalHost(), port);
			Thread.sleep(5);
			printOutToAll(EXIT_OPERATION, "", false);
			dummyClient.close();
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * prints a message to all the clients on this server.
	 * 
	 * @param Msg Message to be sent.
	 * @param Unwanted_UserID UserId of the Client who should be excluded from recieving the message. 
	 * @param UserIDFunction true if someone is supposed to be excluded, false instead.
	 */
	public void printOutToAll(String Msg, String Unwanted_UserID, boolean UserIDFunction) {
		clientsList.forEach(handler -> {

			if (UserIDFunction && !handler.UserID.equals(Unwanted_UserID)) {
				handler.sendMessage(Msg);
			} else if (!UserIDFunction) {
				handler.sendMessage(Msg);
			}
		});
	}

	/**
	 * prints a message to a designated User.
	 * 
	 * @param MSG
	 * @param User
	 */
	public void printOutToDedicatedPerson(String MSG, String User) {
		clientsList.forEach(handler -> {
			if (handler.UserID.equals(User)) {
				handler.sendMessage(MSG);
			}
		});

	}

	/**
	 * removes the inactive Client from the list of Users.
	 * 
	 * @param inactiveHandler specifies which handler has to be removed.
	 */
	public void removeInactiveClient(ClientHandler inactiveHandler) {
		clientsList.remove(inactiveHandler);
	}

	/**
	 * 
	 * @return list of Online Users.
	 */
	public String getClientsID() {
		clientsList.forEach(i -> {
			tempUsersID += String.format("\n%s", i.UserID);
		});
		return tempUsersID.trim();
	}

	/**
	 * sets the serverState to either true (good health) or false (bad health).
	 * 
	 * @param state 
	 */
	public void setServerState(boolean state) {
		this.isRunning = state;
	}

	/**
	 * 
	 * @return wether the server is online (good health).
	 */
	public boolean getServerState() {
		return isRunning;
	}
}
