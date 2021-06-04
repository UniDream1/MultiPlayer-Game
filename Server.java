package serverFrame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class Server {

	private ServerSocket server;

	private ArrayList<ClientHandler> clientsList = new ArrayList<ClientHandler>();

	private boolean isRunning = true;

	private String tempUsersID;

	private JTextArea InsightArea;
	private int port;

	public Server(JTextArea InsightArea) {
		this.InsightArea = InsightArea;
	}

	public void setInsightAreaText(String msg) {
		InsightArea.setText(InsightArea.getText() + msg);
	}

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
											+ "\n ----------------------\n";
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

	public boolean isRunning() {
		return server != null;
	}

	public void CloseServer() throws IOException {
		if (server != null) {
			server.close();
		}
	}

	@SuppressWarnings("unused")
	public void turnOff() {
		try {
			printOutToAll("Server has shut down!", "", false);
			Thread.sleep(5);
			Socket dummyClient = new Socket(InetAddress.getLocalHost(), port);
			Thread.sleep(5);
			printOutToAll("EXIT:OPERATION", "", false);
			isRunning = false;
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		printOutToAll("Server.state = false", tempUsersID, false);
		try {
			Thread.sleep(30);
			isRunning = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void printOutToAll(String Msg, String UserID, boolean UserIDFunction) {
		clientsList.forEach(handler -> {

			if (UserIDFunction && !handler.UserID.equals(UserID)) {
				handler.sendMessage(Msg);
			} else if (!UserIDFunction) {
				handler.sendMessage(Msg);
			}
		});
	}

	public void printOutToDedicatedPerson(String MSG, String Person) {
		clientsList.forEach(handler -> {
			if (handler.UserID.equals(Person)) {
				handler.sendMessage(MSG);
			}
		});
	}

	public void removeInactiveClient(ClientHandler inactiveHandler) {
		clientsList.remove(inactiveHandler);
	}

	public String getClientsID() {
		clientsList.forEach(i -> {
			tempUsersID += String.format("\n%s", i.UserID);
		});
		return tempUsersID.trim();
	}

	public void setServerState(boolean state) {
		this.isRunning = state;
	}

	public boolean getServerState() {
		return isRunning;
	}
}
