package tcpchat.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
//import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private Map<Socket, Boolean> clientsAlive = new HashMap<>();
	private ServerSocket m_server_socket;
//	private Socket m_socket;
	private Socket client_socket;
	public List<Socket> sockets = new ArrayList<>();
//	private String input = "{}";
	public ObjectMapper objectMapper = new ObjectMapper();
//	public ChatMessage cm = objectMapper.readValue(input, ChatMessage.class);
//	public ArrayList<Socket> client_sockets = new ArrayList<>();
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

	protected synchronized ArrayList<ClientConnection> getConnectedClients() {
		return m_connectedClients;
	}

	private Server(int portNumber) throws IOException {
		m_server_socket = new ServerSocket(portNumber);
	}

	private void listenForClientMessages() throws IOException, ClassNotFoundException {
		System.out.println("Waiting for client messages... ");
		do {
			try {
				client_socket = m_server_socket.accept();
				System.out.println(m_server_socket.getReceiveBufferSize());
				System.out.println("received something");
				if (!sockets.contains(client_socket)) {
					individualListenForMessages(client_socket, this);
				}
				client_socket = null;
			} catch (IOException e) {
				System.out.println("serverListenException");
				e.printStackTrace();
				break;
			}
		} while (true);
	}

	protected synchronized boolean checkIfNameExists(String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return true;
			}
		}
		return false;
	}

	protected synchronized void disconnectClient(ClientConnection c) {
		try {
			m_connectedClients.remove(c);
			clientsAlive.put(c.getSocket(), false);
			System.out.println(c.getName() + " is alive: " + clientsAlive.get(c.getSocket()));
			ChatMessage dcMessage = new ChatMessage();
			dcMessage.setMessage(c.getName() + " disconnected");
			dcMessage.setName(c.getName());
			dcMessage.setCommand("generic");
			broadcast(dcMessage);
			sockets.remove(c.getSocket());
			System.out.println("helloooooooooo dc");
			
		} catch (SocketException e) {
			System.out.println("SOCKET E EXCEPTION DISCONNECT CLIENT!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void individualListenForMessages(Socket sock, Server ser) {
		Socket socket = sock;
		Server server = ser;
		Thread thread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						ObjectMapper om = new ObjectMapper();
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);

						// deserialize
						Object obj = ois.readObject();
						ChatMessage cm = om.readValue(obj.toString(), ChatMessage.class);
						System.out.println("hey");
						if (!sockets.contains(socket)) {
							System.out.println("huoo");
							addClient(cm.getName(), socket);
						}
						if (sockets.contains(socket)) {
							commandController = new CommandController(cm, server);
							commandController.makeDecision();
						} else {
							System.out.println("break");
							break;
						}
					} catch (SocketException e) {
						// remove the client
						ClientConnection c;
						for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
							c = itr.next();
							if (c.hasSocket(socket)) {
								System.out.println("DISCONNECT THE CLIENT!");
								disconnectClient(c);
								break;
							}
						}
						System.out.println("client not foun");
						break;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					} catch (JsonParseException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}

				} while (clientsAlive.get(socket));
			}
		};
		thread.start();
	}

	public synchronized boolean addClient(String name, Socket socket)
			throws JsonParseException, JsonMappingException, IOException {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		sockets.add(socket);
		clientsAlive.put(socket, true);
		m_connectedClients.add(new ClientConnection(name, socket));
		return true;
	}

	public synchronized void sendPrivateMessage(ChatMessage cm) {
		try {
			ClientConnection c;
			for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
				c = itr.next();
				if (c.hasName(cm.getRecipent())) {
					c.sendMessage(cm, c.getSocket());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void broadcast(ChatMessage cm) throws IOException {
		int i = 0;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(cm, sockets.get(i));
			i++;
		}
	}
}
