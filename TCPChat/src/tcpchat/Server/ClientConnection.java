/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author brom
 */
public class ClientConnection {
	
	private String m_name;
	private Socket m_socket;
	private String input = "{}";

	public ObjectMapper om = new ObjectMapper();

	ChatMessage cm;
	
	protected Socket getSocket() {
		return m_socket;
	}
	
	public ClientConnection(String name, Socket socket) throws JsonParseException, JsonMappingException, IOException {
		m_name = name;
		m_socket = socket;
		this.cm = om.readValue(input, ChatMessage.class);
	}

	public void sendMessage(ChatMessage cm, Socket socket) throws IOException {
		OutputStream os = m_socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		String serializedClass = om.writeValueAsString(cm);
		System.out.println("sent to " + cm.getName() + ": " + serializedClass);
		oos.writeObject(serializedClass);
		System.out.println("Message Sent from Server to Client - " + m_name + "\n" + serializedClass);
	}
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		System.out.println("m_name = " + m_name);
		m_name = name;
	}
	
	public boolean hasSocket(Socket socket) {
		return socket.equals(m_socket);
	}

	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}

}
