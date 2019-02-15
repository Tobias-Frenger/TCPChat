package tcpchat.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private ServerSocket m_server_socket;
	private Socket m_socket;
	private Socket client_socket;
	private List<Socket> sockets = new ArrayList<>();
//	private String input = "{}";
	public ObjectMapper objectMapper = new ObjectMapper();
//	public ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
	public ArrayList<Socket> client_sockets = new ArrayList<>();
	private CommandController commandController;
	private String name;

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
				if (!sockets.contains(client_socket)) {
					
					System.out.println(
							"adding client socket " + client_socket.getInetAddress() + " - " + client_socket.getPort());
					sockets.add(client_socket);
					individualListenForMessages(client_socket, this);
				}
				System.out.println("s to .> " + client_socket.getPort());
				System.out.println("SERVER: post accept");
			} catch (IOException e) {
				System.out.println("serverListenException");
				e.printStackTrace();
				break;
			}
		} while (true);
	}

	private void individualListenForMessages(Socket sock, Server ser) {
		Socket socket = sock;
		Server server = ser;
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						ObjectMapper om = new ObjectMapper();
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						
						// deserialize
						Object obj = ois.readObject();
						ChatMessage cm = om.readValue(obj.toString(), ChatMessage.class);
						addClient(cm.getName(), socket);
						
						commandController = new CommandController(cm, server);
						commandController.makeDecision();
						
					}catch(SocketException e) {
						// remove from m_connectedClients as well
						ClientConnection c;
						for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
							c = itr.next();
							if (c.hasSocket(socket)) {
								m_connectedClients.remove(c);
								sockets.remove(socket);
								break;
							}
						}
						break;
					}  catch (ClassNotFoundException e) {
						System.out.println("CLASS NOT FOUND EXCEPTION");
						e.printStackTrace();
						break;
					} catch (JsonParseException e) {
						System.out.println("JsonParse");
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				} 
			}
		};
		thread.start();
	}

	public boolean addClient(String name, Socket socket)
			throws JsonParseException, JsonMappingException, IOException {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		m_connectedClients.add(new ClientConnection(name, socket));
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

	public void broadcast(ChatMessage cm) throws IOException {
		int i = 0;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(cm, sockets.get(i));
			i++;
		}
	}
}
