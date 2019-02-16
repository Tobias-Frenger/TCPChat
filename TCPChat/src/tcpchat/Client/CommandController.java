package tcpchat.Client;

import java.io.IOException;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/*
 * This class contains three methods that detects, trims and, sets the command for the message.
 * Note that the class only handles outgoing messages.
 * @method detectCommand 	- calls the removeKeyWord method and then calls the setCommand method.
 * @method removeKeyWord 	- simply removes the key word from the message.
 * @method setCommand		- sets the command for the message.
 * @method detectRecipent	- is used to set the recipient in the outgoing message.
 * @method changeName		- is used to change the name of the client; client-side only.
 * 
 * @author a16tobfr
 */
public class CommandController {
	ChatMessage cm;
	Client client;

	public CommandController(ChatMessage cm, Client client) {
		this.cm = cm;
		this.client = client;
	}

	protected void detectCommand() {
		if (cm.getMessage().startsWith("/tell")) {
			removeKeyWord("/tell");
			setCommand(CommandType.TELL);
		} else if (cm.getMessage().startsWith("/list")) {
			removeKeyWord("/list");
			setCommand(CommandType.LIST);
		} else if (cm.getMessage().startsWith("/help")) {
			removeKeyWord("/help");
			setCommand(CommandType.HELP);
		} else if (cm.getMessage().startsWith("/join")) {
			client.listenForMessages = true;
			try {
				client.setConnection(new ServerConnection("127.0.0.1", 25001, client));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			removeKeyWord("/join");
			setCommand(CommandType.JOIN);
		} else if (cm.getMessage().startsWith("/leave")) {
			removeKeyWord("/leave");
			setCommand(CommandType.LEAVE);
		} else if (cm.getMessage().startsWith("/qotd")) {
			removeKeyWord("/qotd");
			setCommand(CommandType.QOTD);
		} else if (cm.getMessage().startsWith("/ping")) {
			removeKeyWord("/ping");
			setCommand(CommandType.PING);
		} else if (cm.getMessage().startsWith("/rename")) {
			removeKeyWord("/rename");
			setCommand(CommandType.RENAME);
		} else if (cm.getMessage().startsWith("/connect")){
			removeKeyWord("/connect");
			setCommand(CommandType.CONNECT);
		} else {
			setCommand(CommandType.GENERIC);
		}
	}

	private void detectRecipient() {
		while (cm.getMessage().startsWith(" ")) {
			cm.setMessage(cm.getMessage().replaceFirst(" ", ""));
		}
		String temp2;
		int index = cm.getMessage().indexOf(" ");
		if (index > -1) { // Check if there is more than one occurrence.
			temp2 = cm.getMessage().substring(0, index); // Extract first word from message.
		} else {
			temp2 = cm.getMessage(); // temp2 is the first word.
		}
		cm.setRecipent(temp2);
		System.out.println("{sending to -> " + temp2 + "}");
	}

	private void changeName() {
		while (cm.getMessage().startsWith(" ")) {
			cm.setMessage(cm.getMessage().replaceFirst(" ", ""));
		}
		String temp2;
		int index = cm.getMessage().indexOf(" ");
		if (index > -1) { // Check if there is more than one occurrence.
			temp2 = cm.getMessage().substring(0, index); // Extract the first word from message.
		} else {
			temp2 = cm.getMessage(); // temp2 is the first word.
		}
		cm.setRecipent(temp2);
		System.out.println(temp2 + " is my new name!");
		client.getGUI().setTitle(temp2);
		client.changeName(temp2);
	}

	private void removeKeyWord(String keyWord) {
		switch (keyWord) {
		case "/tell":
			cm.setMessage(cm.getMessage().replaceFirst("/tell", ""));
			detectRecipient();
			break;
		case "/list ":
			cm.setMessage(cm.getMessage().replaceFirst("/list", ""));
			break;
		case "/help":
			cm.setMessage(cm.getMessage().replaceFirst("/help", ""));
			break;
		case "/join":
			cm.setMessage(cm.getMessage().replaceFirst("/join", ""));
			break;
		case "/leave":
			cm.setMessage(cm.getMessage().replaceFirst("/leave", ""));
			break;
		case "/qotd":
			cm.setMessage(cm.getMessage().replaceFirst("/qotd", ""));
			break;
		case "/ping":
			cm.setMessage(cm.getMessage().replaceFirst("/ping", ""));
			break;
		case "/rename":
			cm.setMessage(cm.getMessage().replaceFirst("/rename", ""));
			changeName();
			break;
		case "/connect":
			cm.setMessage(cm.getMessage().replaceFirst("/connect", ""));
		default:
			break;
		}
		while (cm.getMessage().startsWith(" ")) {
			cm.setMessage(cm.getMessage().replaceFirst(" ", ""));
		}
	}

	private void setCommand(CommandType ct) {
		switch (ct) {
		case GENERIC:
			cm.setCommand("generic");
			break;
		case TELL:
			cm.setCommand("tell");
			break;
		case LIST:
			cm.setCommand("list");
			break;
		case HELP:
			cm.setCommand("help");
			break;
		case JOIN:
			cm.setCommand("join");
			break;
		case LEAVE:
			cm.setCommand("leave");
			break;
		case QOTD:
			cm.setCommand("qotd");
			break;
		case PING:
			cm.setCommand("ping");
			break;
		case RENAME:
			cm.setCommand("rename");
			break;
		case CONNECT:
			cm.setCommand("connect");
		default:
			cm.setCommand("generic");
			break;
		}
	}
}
