package serverFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread {

	private static final String EXIT_OPERATION = "69E_O";
	private static final String MEMBERS_REQUEST = "420M_R";

	private Socket client;

	private Server server;

	private PrintWriter output;

	private BufferedReader input;

	private String message;
	private String time;

	protected String UserID = "";

	public ClientHandler(Socket client, Server server) throws IOException {
		this.client = client;
		this.server = server;

		output = new PrintWriter(this.client.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

		UserID = input.readLine();

		time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
		server.setInsightAreaText(String.format("\n%s: %s joined the server!", time, UserID));
	}

	public void run() {
		try {
			while (true) {
				message = input.readLine();
				if (message.equals(EXIT_OPERATION)) {
					sendMessage(EXIT_OPERATION);
					break;

				} else if (message.equals(MEMBERS_REQUEST)) {
					String Members = String.format("%s | %s", MEMBERS_REQUEST, server.getClientsID());
					sendMessage(Members);

				} else if (message.startsWith("CONTROL:")) {
					server.printOutToAll(message, this.UserID, true);

				} else if (message.startsWith("@_")) {
					String personID = message.substring(2, message.indexOf(" "));

					time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
					//@formatter:off
					server.printOutToDedicatedPerson(String.format("[%s]%s: %s", UserID, time,message.substring(message.indexOf(" ") + 1)) ,personID);									
					//@formatter:on

				} else {
					// All Chat

					time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

					server.setInsightAreaText(String.format("\n[%s] %s : %s", this.UserID, time, message));
					server.printOutToAll(String.format("[%s] %S: %s", this.UserID, time, message), UserID, true);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (server.getServerState()) {
					//@formatter:off
					time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
					String exitAlert = String.format(
							" \n------------------------------------\n"
							+ "| %s: %s has left the server!  |" 
							+ "\n ------------------------------------  "
							,time, UserID);
					//@formatter:on

					server.printOutToAll(exitAlert, UserID, true);
					server.setInsightAreaText(exitAlert);
				}

				output.close();
				input.close();
				client.close();

				server.removeInactiveClient(this);

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void sendMessage(String Msg) {
		output.println(Msg);
		output.flush();
	}

}
