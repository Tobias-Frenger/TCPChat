package tcpchat.Client;

//import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Client implements ActionListener {

	private String m_name = null;
	private final ChatGUI m_GUI;
	private ServerConnection m_connection = null;
	private ChatMessage cm = new ChatMessage();
	private CommandController commandController = new CommandController(cm);
	public boolean listenForMessages = true;

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
	}

	@SuppressWarnings("deprecation")
	private Client(String userName) throws JsonParseException, JsonMappingException, IOException {
		m_name = userName;
		System.out.println(m_name);
		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
		cm.setName(m_name);
		cm.setId(UUID.randomUUID().toString());
		cm.setTimeStamp(System.currentTimeMillis());
		
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
			m_GUI.displayMessage("Today is unknown day");
			break;
		}
		long hour = date.getHours();
		long min = date.getMinutes();
		String t = "" + hour + ":" + min;
		m_GUI.displayMessage("Started client at: " + t);

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

	protected ChatGUI getGUI() {
		return m_GUI;
	}

	private void listenForServerMessages() {
		do {
			m_connection.receiveChatMessage();
			System.out.println("client received message");
		} while (listenForMessages);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits enter
	// in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		cm.setTimeStamp(System.currentTimeMillis());
		cm.setId(UUID.randomUUID().toString());
		cm.setMessage(m_GUI.getInput());
		// check if input contained a command
		commandController.detectCommand();
		System.out.println("2action: " + cm.toString());
		m_connection.sendChatMessage(cm);
		m_GUI.clearInput();
	}
}
