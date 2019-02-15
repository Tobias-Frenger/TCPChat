package tcpchat.Server;

import java.io.IOException;

public class CommandController {
	private ChatMessage cm;
	private Server server;
	
	public CommandController(ChatMessage cm, Server server) {
		this.cm = cm;
		this.server = server;
	}
	
	protected void makeDecision() throws IOException {
		switch (cm.getCommand()) {
		case "generic":
			server.broadcast(cm);
			break;
		case "tell":
			// send private message
			break;
		case "list":
			// send private message with list of participants
			break;
		case "help":
			// change message to display list of possible commands
			break;
		case "leave":
			// disconnect client from the server
			break;
		case "join":
			// reconnect client to the server
			break;
		case "qotd":
			// display qotd message
			// The only true widom is knowing you know nothing - Socrates
			// If you cannot do great things, do small things in a great way - Napoleon Hill
			// The great thing about getting older is that you do not lose all the other ages you've been - Madeleine L'Engle
			// You don't have to be great to start, but you have to start to be great - Zig Ziglar
			// We are what we repeatedly do; excellence, then, is not an act but a habit - Aristotle
			// The grass is greener where you water it - Neil Barringham
			// It does not matter how slowly you go as long as you do not stop - Confusius
			break;
		case "ping":
			// send private message to client
			break;
		case "rename":
			// send private message to client - rename successful
			break;
		case "connect":
			String temp = cm.getName();
			cm.setName("server");
			cm.setMessage(temp + " has connected!");
			server.broadcast(cm);
			
		default:
			// broadcast message
			break;
		}
	}
}
