package tcpchat.Client;

//import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * Methods in this class:
 * @method main
 * @method listenForServerMessages
 * @method getClientMessage
 * @method connectToServer
 * @method changeName
 * @method getClientMessages
 * @method getGUI
 * @method isConnected
 * @method setConnected
 * @method setConnection
 * @method actionPerformed
 * @methods get/setServerIp
 * @methods get/setServerPort
 * 
 * @finalizedBy a16tobfr 
 * Project: TCPChat
 * Date: 17 feb. 2019
 */
public class Client implements ActionListener {
	private final ChatGUI m_GUI;
	private String m_name = null;
	private ServerConnection m_connection = null;
	
	private ChatMessage chatMessage = new ChatMessage();
	private CommandController commandController = new CommandController(chatMessage, this);
	
	private boolean isConnected = false;
	private static String serverIp;
	private static int serverPort;
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 3) {
			System.err.println("Usage: java Client serverhostname serverportnumber username");
			System.exit(-1);
		}
		try {
			Client instance = new Client(args[2]);
			instance.connectToServer(args[0], Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
		setServerIp(args[0]);
		setServerPort(args[1]);
	}
	
	@SuppressWarnings("deprecation")
	private Client(String userName) throws JsonParseException, JsonMappingException, IOException {
		m_name = userName;
		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
		chatMessage.setName(m_name);
		chatMessage.setTimeStamp(System.currentTimeMillis());

		Date date = new Date();

		switch (date.getDay()) {
		case 0:
			m_GUI.displayMessage("Today is Sunday");
			break;
		case 1:
			m_GUI.displayMessage("Today is Monday");
			break;
		case 2:
			m_GUI.displayMessage("Today is Tuesday");
			break;
		case 3:
			m_GUI.displayMessage("Today is Wednesday");
			break;
		case 4:
			m_GUI.displayMessage("Today is Thursday");
			break;
		case 5:
			m_GUI.displayMessage("Today is Friday");
			break;
		case 6:
			m_GUI.displayMessage("Today is Saturday");
			break;
		default:
			m_GUI.displayMessage("Today is unknown");
			break;
		}
		long hour = date.getHours();
		long min = date.getMinutes();
		String t = "" + hour + ":" + min;
		m_GUI.displayMessage("Started client at: " + t);

	}

	protected ServerConnection getConnection() {
		return m_connection;
	}

	protected void setConnection(ServerConnection sc) {
		m_connection = sc;
	}

	private void connectToServer(String hostName, int port)
			throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
		// Create a new server connection
		m_connection = new ServerConnection(hostName, port, this);
		if (m_connection.handshake(m_name)) {
			listenForServerMessages();
		} else {
			System.err.println("Unable to connect to server");
		}
	}

	protected void listenForServerMessages() {
		Thread thread = new Thread() {
			public void run() {
				do {
					m_connection.receiveChatMessage();
				} while (isConnected);
			}
		};thread.start();
	}
	
	protected void changeName(String name) {
		m_name = name;
	}
	
	protected ChatMessage getClientMessage() {
		return chatMessage;
	}
	
	protected ChatGUI getGUI() {
		return m_GUI;
	}
	
	protected boolean isConnected() {
		return isConnected;
	}

	protected void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public static String getServerIp() {
		return serverIp;
	}

	public static void setServerIp(String serverIp) {
		Client.serverIp = serverIp;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static void setServerPort(String serverPort) {
		Client.serverPort = Integer.parseInt(serverPort);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits enter
	// in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		chatMessage.setTimeStamp(System.currentTimeMillis());
		chatMessage.setMessage(m_GUI.getInput());
		chatMessage.setName(m_name);
		// check if input contained a command
		commandController.detectCommand();
		m_connection.sendChatMessage(chatMessage);
		m_GUI.clearInput();
	}
}
