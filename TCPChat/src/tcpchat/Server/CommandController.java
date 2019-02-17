package tcpchat.Server;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * 
 * Class description:
 * This class handles the commands of incoming messages from the client.
 * Each client thread has its own instance of this class so synchronization
 * is not needed.
 * 
 * Methods in this class:
 * @method makeDecision
 *
 * @author a16tobfr 
 * Project: TCPChat
 * Date: 17 feb. 2019
 */
public class CommandController {
	private ChatMessage cm;
	private Server server;

	protected CommandController(ChatMessage cm, Server server) {
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
			boolean found = false;
			// send a private message to the recipient and the sender.
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				if (c.hasName(cm.getRecipent())) {
					// send private message to recipient.
					cm.setMessage(cm.getMessage().replace(cm.getRecipent(), ""));
					String temp = cm.getMessage();
					cm.setMessage(cm.getName() + " whispers -> " + cm.getMessage());
					server.sendPrivateMessage(cm);
					// send private message to sender.
					cm.setMessage("You whispered " + cm.getRecipent() + " -> " + temp);
					cm.setRecipent(cm.getName());
					server.sendPrivateMessage(cm);
					found = true;
					break;
				}
			}
			// send private message to sender that recipient was not found.
			if (!found) {
				cm.setMessage("Client with name - [ " + cm.getRecipent() + " ] - was not found");
				cm.setRecipent(cm.getName());
				server.sendPrivateMessage(cm);
			}
			break;
		case "list":
			String top = "Clients connected:\n";
			String bottom = "********************";
			cm.setMessage(top);
			cm.setRecipent(cm.getName());
			// get the name of each client that is connected and add to the message.
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				cm.setMessage(cm.getMessage() + c.getName() + "\n");
			}
			// send a private message with list of participants to the sender.
			cm.setMessage(cm.getMessage() + bottom);
			server.sendPrivateMessage(cm);
			break;
		case "help":
			cm.setRecipent(cm.getName());
			cm.setMessage("\n/tell \t- send a private message\n" + "/list \t- get a list of connected clients\n"
					+ "/leave \t- disconnect from the server\n" + "/join \t- reconnect to the server\n"
					+ "/qotd \t- read the quote of the day\n" + "/rename \t- change your name\n");
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
					break;
				}
			}
			break;
		case "join":
			cm.setMessage("[SERVER] -> " + cm.getName() + " has reconnected!");
			server.broadcast(cm);
			;
			break;
		case "qotd":
			// Get the quote of the day and send it back to the sender.
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
		case "rename":
			for (Iterator<ClientConnection> itr = server.getConnectedClients().iterator(); itr.hasNext();) {
				c = itr.next();
				// requested name change is not allowed.
				if (server.checkIfNameExists(cm.getRecipent())) {
					cm.setCommand("renameDuplicate");
					cm.setMessage("Client with that name already exists");
					cm.setRecipent(cm.getName());
					server.sendPrivateMessage(cm);
					break;
				}
				// change name of the client; server-side.
				else {
					if (c.hasName(cm.getName())) {
						while (cm.getMessage().startsWith(" ")) {
							cm.setMessage(cm.getMessage().replaceFirst(" ", ""));
						}
						String temp1 = c.getName();
						c.setName(cm.getRecipent());
						cm.setMessage(" - [" + temp1 + " changed name to " + c.getName() + "] -");
						server.broadcast(cm);
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