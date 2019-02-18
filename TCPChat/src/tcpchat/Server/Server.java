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

/**
 * 
 * Methods in this class:
 * @method main
 * @method getConnectedClients
 * @method listenForClientMessages
 * @method checkIfNameExists
 * @method disconnectClient
 * @method individualListenForMessages
 * @method addClient
 * @method sendPrivateMessage
 * @method broadcast
 *
 * @finalizedBy a16tobfr
 * Project: TCPChat
 * Date: 17 feb. 2019
 */
public class Server {
	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private Map<Socket, Boolean> clientsAlive = new HashMap<>();
	public List<Socket> sockets = new ArrayList<>();
	private ServerSocket m_server_socket;
	private Socket client_socket;
	private CommandController commandController;

	private Server(int portNumber) throws IOException {
		m_server_socket = new ServerSocket(portNumber);
	}

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

	private void listenForClientMessages() throws IOException, ClassNotFoundException {
		System.out.println("Waiting for client messages... ");
		do {
			try {
				client_socket = m_server_socket.accept();
				if (!sockets.contains(client_socket)) {
					individualListenForMessages(client_socket, this);
				}
				client_socket = null;
			} catch (IOException e) {
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
			ChatMessage dcMessage = new ChatMessage();
			dcMessage.setMessage(c.getName() + " disconnected");
			dcMessage.setName(c.getName());
			dcMessage.setCommand("generic");
			broadcast(dcMessage);
			sockets.remove(c.getSocket());

		} catch (SocketException e) {
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
						// receive message on the socket.
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);

						// deserialize the message
						Object obj = ois.readObject();
						ChatMessage cm = om.readValue(obj.toString(), ChatMessage.class);
						cm.setTimeStamp(System.currentTimeMillis() + 1);
						// if socket is unknown, add client.
						if (!sockets.contains(socket)) {
							addClient(cm.getName(), socket);
						}
						if (sockets.contains(socket)) {
							// make a decision based on the command of the message
							commandController = new CommandController(cm, server);
							commandController.makeDecision();
						} else {
							break;
						}
					} catch (SocketException e) {
						// remove the client
						ClientConnection c;
						for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
							c = itr.next();
							if (c.hasSocket(socket)) {
								try {
									socket.shutdownInput();
									disconnectClient(c);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								break;
							}
							e.printStackTrace();
						}
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

	protected synchronized boolean addClient(String name, Socket socket)
			throws JsonParseException, JsonMappingException, IOException {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				// Already exists a client with this name
				return false;
			}
		}
		sockets.add(socket);
		clientsAlive.put(socket, true);
		m_connectedClients.add(new ClientConnection(name, socket));
		return true;
	}

	protected synchronized void sendPrivateMessage(ChatMessage cm) {
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

	protected synchronized void broadcast(ChatMessage cm) throws IOException {
		int i = 0;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(cm, sockets.get(i));
			i++;
		}
	}
}