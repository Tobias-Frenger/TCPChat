package tcpchat.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private ServerSocket m_server_socket;
	private Socket m_socket;
	private Socket client_socket;
//	private String input = "{}";
	public ObjectMapper objectMapper = new ObjectMapper();
//	public ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
	public ArrayList<Socket> client_sockets = new ArrayList<>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 1) {
			System.err.println("Usage: java Server portnumber");
			System.exit(-1);
		}
		try {
			Server instance = new Server(Integer.parseInt(args[0]));
			instance.listenForClientMessages();
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Server(int portNumber) throws IOException {
		m_server_socket = new ServerSocket(portNumber);
	}

	private void listenForClientMessages() throws IOException, ClassNotFoundException {
		System.out.println("Waiting for client messages... ");
		do {
			try {
				
				System.out.println("SERVER: pre accept");
				client_socket = m_server_socket.accept();
				client_sockets.add(client_socket);
//				individualListenForMessages(client_sockets.get(client_sockets.size() - 1));
//				s.getInputStream();
				ObjectMapper om = new ObjectMapper();
				InputStream is = client_socket.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				ObjectInputStream ois = new ObjectInputStream(dis);

				Object obj = ois.readObject();
				JsonNode jn = om.readTree(obj.toString());
				ChatMessage cm1 = om.readerFor(ChatMessage.class).readValue(jn);
				System.out.println(cm1.toString());
				cm1.setMessage("you made it!");
				cm1.setName("SERVER");
				String serializedMessage = om.writeValueAsString(cm1);
				OutputStream os = client_socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				
				oos.writeObject(serializedMessage);
				System.out.println("SERVER: post accept");
			} catch (IOException e) {
				System.out.println("serverListenException");
				e.printStackTrace();
				break;
			}
		} while (true);
	}

	private void individualListenForMessages(Socket socket) {
		Socket s = socket;
		Thread thread = new Thread() {
			public void run() {
				do {
//					try {
//						
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (ClassNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

				} while (true);
			}
		};
		thread.start();
	}

	public boolean addClient(String name, InetAddress address, int port)
			throws JsonParseException, JsonMappingException, IOException {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		m_connectedClients.add(new ClientConnection(name, address, port));
		return true;
	}

//	public void sendPrivateMessage(String message, String name) {
//		ClientConnection c;
//		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
//			c = itr.next();
//			if (c.hasName(name)) {
//				c.sendMessage(message, m_socket);
//			}
//		}
//	}

//	public void broadcast(String message) {
//		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
//			itr.next().sendMessage(message, m_socket);
//		}
//	}
}
