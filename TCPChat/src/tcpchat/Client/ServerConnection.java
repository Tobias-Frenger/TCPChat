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
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * Methods in this class:
 * @method ServerConnection
 * @method handshake
 * @method receiveChatMessage
 * @method sendChatMessage
 * 
 * @author brom 
 * @finalizedBy a16tobfr
 */
public class ServerConnection {
	private Socket m_socket = null;
	private String input = "{}";
	private ObjectMapper objectMapper = new ObjectMapper();
	private ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
	private Client client;

	protected ServerConnection(String hostName, int port, Client client)
			throws JsonParseException, JsonMappingException, IOException {
		this.client = client;
		m_socket = new Socket(hostName, port);
		cm.setMessage("/connect");
		cm.setCommand("connect");
		cm.setTimeStamp(System.currentTimeMillis());
	}

	protected boolean handshake(String name) {
		cm.setName(name);
		sendChatMessage(cm);
		receiveChatMessage();
		return true;
	}

	@SuppressWarnings("deprecation")
	protected void receiveChatMessage() {
		// receive message.
		try {
			// receive message on socket.
			InputStream is = m_socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			// deserialize message.
			ObjectMapper om = new ObjectMapper();
			Object obj = ois.readObject();
			ChatMessage cm;
			cm = om.readValue(obj.toString(), ChatMessage.class);
			cm.setTimeStamp(System.currentTimeMillis() + 1);
			// handle the message based on it's command.
			IncomingCommand ic = new IncomingCommand(client);
			ic.handleIncomingMessage(cm);
			// print the message to the GUI.
			Date date = new Date();
			client.getGUI().displayMessage("[" + date.getHours() + ":" + date.getMinutes() + "] " + cm.getMessage());
		} catch (SocketException e) {
			client.getGUI().displayMessage("Possible server failure\n" + "Try restarting your client");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void sendChatMessage(ChatMessage chatMessage) {
		try {
			// Serialize the chatMessage.
			ObjectMapper om = new ObjectMapper();
			String serializedMessage;
			serializedMessage = om.writeValueAsString(chatMessage);
			// Send the message to the server.
			OutputStream os = m_socket.getOutputStream();
			ObjectOutputStream objectos = new ObjectOutputStream(os);
			objectos.writeObject(serializedMessage);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
