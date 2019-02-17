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
 * Methods in this class:
 * @method ClientConnection
 * @method SendMessage
 * @method hasSocket
 * @method hasName
 * @methods get/setName
 * 
 * @author brom
 * @finalizedBy a16tobfr
 */
public class ClientConnection {
	private String m_name;
	private Socket m_socket;
	private String input = "{}";

	protected ObjectMapper om = new ObjectMapper();
	protected ChatMessage cm;

	protected Socket getSocket() {
		return m_socket;
	}

	protected ClientConnection(String name, Socket socket) throws JsonParseException, JsonMappingException, IOException {
		m_name = name;
		m_socket = socket;
		this.cm = om.readValue(input, ChatMessage.class);
	}

	protected void sendMessage(ChatMessage cm, Socket socket) throws IOException {
		OutputStream os = m_socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		cm.setTimeStamp(System.currentTimeMillis());
		String serializedClass = om.writeValueAsString(cm);
		oos.writeObject(serializedClass);
	}

	protected String getName() {
		return m_name;
	}

	protected void setName(String name) {
		m_name = name;
	}

	protected boolean hasSocket(Socket socket) {
		return socket.equals(m_socket);
	}

	protected boolean hasName(String testName) {
		return testName.equals(m_name);
	}

}