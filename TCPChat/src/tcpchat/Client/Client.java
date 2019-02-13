package tcpchat.Client;

//import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client implements ActionListener {

	private String m_name = null;
	private final ChatGUI m_GUI;
	private ServerConnection m_connection = null;
	private ChatMessage cm = new ChatMessage();
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

	private Client(String userName) throws JsonParseException, JsonMappingException, IOException{
		m_name = userName;
		System.out.println(m_name);
		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
		cm.setName(m_name);
		cm.setId(UUID.randomUUID().toString());
//		cm.setIp(m_connection.getM_ip());
//		cm.setPort(m_connection.getM_serverPort());
		cm.setTimeStamp(System.currentTimeMillis());
	}

	private void connectToServer(String hostName, int port) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
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
	
	private void listenForServerMessages() throws IOException, ClassNotFoundException {
		InputStream is = m_connection.getSocket().getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		// deserialize message
		ObjectMapper om = new ObjectMapper();
		Object obj = ois.readObject();
		JsonNode jn = om.readTree(obj.toString());
		ChatMessage cm = om.readerFor(ChatMessage.class).readValue(jn);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits enter
	// in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		// Since the only possible event is a carriage return in the text input field,
		// the text in the chat input field can now be sent to the server.
//		try {
//			JSONObject jasObj = new JSONObject();
//			jasObj.put("Message", m_GUI.getInput());
//			getSM().setMessage(m_GUI.getInput());
//			m_connection.sendChatMessage(getSM());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		cm.setTimeStamp(System.currentTimeMillis());
		cm.setId(UUID.randomUUID().toString());
		cm.setMessage(m_GUI.getInput());
		try {
			m_connection.sendChatMessage(cm);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		m_GUI.displayMessage(m_name + " say: " + m_GUI.getInput());
		m_GUI.clearInput();
	}
}
