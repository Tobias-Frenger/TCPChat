/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author brom
 * 
 */
public class ServerConnection {
	private Socket m_socket = null;

	private int m_serverPort = -1;
	private String input = "{}";
	public ObjectMapper objectMapper = new ObjectMapper();
	public ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
//	private ObjectMapper objectMapper = new ObjectMapper();
	private InetAddress m_serverAddress;
	private Client client;
//	private String message;
	private String m_ip;

	public ServerConnection(String hostName, int port, Client client)
			throws JsonParseException, JsonMappingException, IOException {
		setServerPort(port);
		this.client = client;
		this.setIp(hostName);
		try {
			m_serverAddress = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		m_socket = new Socket(hostName, port);

		cm.setId(UUID.randomUUID().toString());
		cm.setIp(hostName);
		cm.setMessage("/connect");
		cm.setCommand("connect");
		cm.setTimeStamp(System.currentTimeMillis());
		cm.setPort(port);
	}
	
	public boolean handshake(String name) {
		cm.setName(name);
		sendChatMessage(cm);
		receiveChatMessage();
		return true;
	}

	@SuppressWarnings("deprecation")
	public void receiveChatMessage() {
		// receive message
		try {
			System.out.println("1");
			InputStream is = m_socket.getInputStream();
			System.out.println("2");
			DataInputStream dis = new DataInputStream(is);
			System.out.println("3");
			ObjectInputStream ois = new ObjectInputStream(is);
			System.out.println("4");
			// deserialize message
			ObjectMapper om = new ObjectMapper();
			Object obj = ois.readObject();
			ChatMessage cm;
			System.out.println("hey");
			cm = om.readValue(obj.toString(), ChatMessage.class);
			System.out.println(cm.getMessage() + " --");
			if (cm.getCommand().equals("rename")) {
				System.out.println("RECIPENT IS ! : " + cm.getRecipent());
				client.getClientMessage().setName(cm.getRecipent());
			} else if (cm.getCommand().equals("renameDuplicate")) {
				client.changeName(cm.getName());
				client.getGUI().setTitle("Chat client for " + cm.getName());
				client.getClientMessage().setName(cm.getName());
			}
			String serializedClass = om.writeValueAsString(cm);
			System.out.println("received: " + serializedClass);
			Date date = new Date();
			client.getGUI().displayMessage("[" + date.getHours() + ":" + date.getMinutes() + "] " + cm.getMessage());
		} catch (SocketException e) {
			client.listenForMessages = false;
			client.getGUI().displayMessage("Possible server failure\n" + "Try restarting your client");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void sendChatMessage(ChatMessage chatMessage) {
		try {
			// Serialize the chatMessage
			ObjectMapper om = new ObjectMapper();
			String serializedMessage;
			serializedMessage = om.writeValueAsString(chatMessage);
			OutputStream os;
			os = m_socket.getOutputStream();
			ObjectOutputStream objectos = new ObjectOutputStream(os);
			objectos.writeObject(serializedMessage);
			System.out.println("message sent: ----- : " + serializedMessage);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Socket getSocket() {
		return m_socket;
	}
	
	protected void setSocket(Socket socket) {
		m_socket = socket;
	}
	
	public String getIp() {
		return m_ip;
	}

	public void setIp(String m_ip) {
		this.m_ip = m_ip;
	}

	public int getServerPort() {
		return m_serverPort;
	}

	public void setServerPort(int m_serverPort) {
		this.m_serverPort = m_serverPort;
	}

}
