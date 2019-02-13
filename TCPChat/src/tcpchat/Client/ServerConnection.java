/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat.Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author brom
 * 
 */
public class ServerConnection {
	private Socket m_socket = null;
	private InetAddress m_serverAddress = null;
	private int m_serverPort = -1;
	private String input = "{}";
	public ObjectMapper objectMapper = new ObjectMapper();
	public ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
//	private ObjectMapper objectMapper = new ObjectMapper();
	private Client client;
	private String message;
	private String m_ip;

	public ServerConnection(String hostName, int port, Client client)
			throws JsonParseException, JsonMappingException, IOException {
		setM_serverPort(port);
		this.client = client;
		this.setM_ip(hostName);
		try {
			m_serverAddress = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		m_socket = new Socket(hostName, port);

		cm.setId(UUID.randomUUID().toString());
		cm.setIp(hostName);
		cm.setMessage("Write a message to the server!");
		cm.setName("W1z@rdUnkn0wN1");
		cm.setTimeStamp(System.currentTimeMillis());
		cm.setPort(port);

		client.getGUI().displayMessage("ID: \t" + cm.getId());
		client.getGUI().displayMessage("IP: \t" + cm.getIp());
		client.getGUI().displayMessage("Port: \t" + cm.getPort());
		client.getGUI().displayMessage("Name: \t" + cm.getName());
		client.getGUI().displayMessage("Time: \t" + cm.getTimeStamp());
		client.getGUI().displayMessage("Message: \t" + cm.getMessage());
	}

	public boolean handshake(String name) throws IOException, ClassNotFoundException {
		cm.setName(name);
		sendChatMessage(cm);
		receiveChatMessage();
		return true;
	}

	public String receiveChatMessage() throws IOException, ClassNotFoundException {
		// receive message
		InputStream is = m_socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		// deserialize message
		ObjectMapper om = new ObjectMapper();
		Object obj = ois.readObject();
		JsonNode jn = om.readTree(obj.toString());
		ChatMessage cm = om.readerFor(ChatMessage.class).readValue(jn);
		client.getGUI().displayMessage(cm.toString());
		System.out.println("post m_socket.getInputStream()");
		return "";
	}

	public void sendChatMessage(ChatMessage chatMessage) throws IOException {
		try {
			//Serialize the chatMessage
			ObjectMapper om = new ObjectMapper();
			String serializedMessage = om.writeValueAsString(chatMessage);
			System.out.println(serializedMessage);
			OutputStream os = m_socket.getOutputStream();
			ObjectOutputStream objectos = new ObjectOutputStream(os);
			objectos.writeObject(serializedMessage);
			System.out.println("message sent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		
		return m_socket;
	}

	public String getM_ip() {
		return m_ip;
	}

	public void setM_ip(String m_ip) {
		this.m_ip = m_ip;
	}

	public int getM_serverPort() {
		return m_serverPort;
	}

	public void setM_serverPort(int m_serverPort) {
		this.m_serverPort = m_serverPort;
	}

//	public void sendChatMessage(ChatMessage chatMessage) throws IOException {
//		OutputStream outputS = m_socket.getOutputStream();
//		ObjectOutputStream objOutS = new ObjectOutputStream(outputS);
//		objOutS.writeObject(json);
//		System.out.println("message sent");
//	}

}
