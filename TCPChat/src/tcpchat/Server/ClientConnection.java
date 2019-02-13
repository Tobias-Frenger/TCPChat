/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tcpchat.Client.Client;

/**
 * 
 * @author brom
 */
public class ClientConnection {
	
	private final String m_name;
	private final InetAddress m_address;
	private final int m_port;
	private String input = "{}";
	public ObjectMapper om = new ObjectMapper();

	ChatMessage cm;
	
	
	public ClientConnection(String name, InetAddress address, int port) throws JsonParseException, JsonMappingException, IOException {
		m_name = name;
		m_address = address;
		m_port = port;
		this.cm = om.readValue(input, ChatMessage.class);
	}

	public void sendMessage(ChatMessage cm, Socket socket) throws IOException {
//		OutputStream out = socket.getOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(out);
//		oos.writeObject(cm);
//		JsonFactory jfactory = new JsonFactory();
//		JsonGenerator jGenerator = jfactory
//		  .createGenerator(out, JsonEncoding.UTF8);
//		ObjectOutputStream oos1 = new ObjectOutputStream(out);
		System.out.println("Message Sent from Server to Client");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		JsonFactory jfactory = new JsonFactory();
//		JsonGenerator jGenerator = jfactory
//		  .createGenerator(stream, JsonEncoding.UTF8);
	}

	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}

}
