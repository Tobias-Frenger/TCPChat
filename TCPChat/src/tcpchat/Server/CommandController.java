package tcpchat.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class CommandController {
	private ChatMessage cm;
	private Server server;

	public CommandController(ChatMessage cm, Server server) {
		this.cm = cm;
		this.server = server;
	}

	@SuppressWarnings({ "deprecation" })
	protected void makeDecision() throws IOException {
		ClientConnection c;
		switch (cm.getCommand()) {
		case "generic":
			cm.setMessage(cm.getName() + " sent -> " + cm.getMessage());
			server.broadcast(cm);
			break;
		case "tell":
			// send private message

			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				if (c.hasName(cm.getRecipent())) {
					// send private message to recipent
					cm.setMessage(cm.getMessage().replace(cm.getRecipent(), ""));
					String temp = cm.getMessage();
					cm.setMessage(cm.getName() + " whispers -> " + cm.getMessage());
					server.sendPrivateMessage(cm);
					// send private message to sender
					cm.setMessage("You whispered " + cm.getRecipent() + " -> " + temp);
					cm.setRecipent(cm.getName());
					server.sendPrivateMessage(cm);
					break;
				}
			}
			// send private message to sender that sender was not found
			System.out.println("name not found");
			break;
		case "list":
			// send private message with list of participants
//			ClientConnection c1;
			String top = "Clients connected:\n";
			String bottom = "********************";
			cm.setMessage(top);
			cm.setRecipent(cm.getName());
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				cm.setMessage(cm.getMessage() + c.getName() + "\n");
				System.out.println(c.getName());
			}
			cm.setMessage(cm.getMessage() + bottom);
			server.sendPrivateMessage(cm);
			break;
		case "help":
			cm.setRecipent(cm.getName());
			cm.setMessage("/tell \t- send a private message\n" + "/list \t- get a list of connected clients\n"
					+ "/leave \t- disconnect from the server\n" + "/join \t- reconnect to the server\n"
					+ "/qotd \t- read the quote of the day\n" + "/ping \t- ping the server\n"
					+ "/rename \t- change your name\n");
			server.sendPrivateMessage(cm);
			break;
		case "leave":
			// disconnect client from the server
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				if (c.hasName(cm.getName())) {
					cm.setCommand("notAlive");
					cm.setRecipent(cm.getName());
					cm.setMessage("You left the server");
					server.sendPrivateMessage(cm);
					server.disconnectClient(c);
					System.out.println("/leave for: " + c.getName() + "-" + cm.getName());
					break;
				}
			}
			break;
		case "join":
			cm.setMessage("[SERVER] -> " + cm.getName() + " has reconnected!");
			server.broadcast(cm);;
			break;
		case "qotd":
			// display qotd message
			cm.setRecipent(cm.getName());
			Date date = new Date();
			int day = date.getDay();
			switch (day) {
			case 0:
				cm.setMessage("'The only true widom is knowing you know nothing'\n- Socrates");
				break;
			case 1:
				cm.setMessage("'If you cannot do great things, do small things in a great way'\n- Napoleon Hill");
				break;
			case 2:
				cm.setMessage(
						"'The great thing about getting older is that you do not lose all the other ages you've been'n- Madeleine L'Engle");
				break;
			case 3:
				cm.setMessage("'You don't have to be great to start, but you have to start to be great'\n- Zig Ziglar");
				break;
			case 4:
				cm.setMessage(
						"'We are what we repeatedly do; excellence, then, is not an act but a habit'\n- Aristotle");
				break;
			case 5:
				cm.setMessage("'The grass is greener where you water it'\n- Neil Barringham");
				break;
			case 6:
				cm.setMessage("'It does not matter how slowly you go as long as you do not stop'\n- Confusius");
				break;
			}
			server.sendPrivateMessage(cm);
			break;
		case "ping":
			// send private message to client
			break;
		case "rename":
//			ClientConnection c2;
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				if (server.checkIfNameExists(cm.getRecipent())) {
					System.out.println("DUPLICATE");
					cm.setCommand("renameDuplicate");
					cm.setMessage("Client with that name already exists");
					cm.setRecipent(cm.getName());
					server.sendPrivateMessage(cm);
					break;
				} else {
					if (c.hasName(cm.getName())) {
						while (cm.getMessage().startsWith(" ")) {
							cm.setMessage(cm.getMessage().replaceFirst(" ", ""));
						}
						System.out.println("1{name change to -> " + c.getName() + "}");
						String temp1 = c.getName();

						c.setName(cm.getRecipent());
						cm.setName(cm.getRecipent());
						cm.setMessage("::{NameChange}:: - " + temp1 + " to " + c.getName());

						System.out.println("2{name change to -> " + c.getName() + "}");
						server.sendPrivateMessage(cm);
						break;
					}
				}
			}	
			break;
		case "connect":
			String temp = cm.getName();
			cm.setMessage("[SERVER] -> " + temp + " has connected!");
			server.broadcast(cm);

		default:
			break;
		}
	}
}
