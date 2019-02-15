package tcpchat.Client;

/*
 * This class contains three methods that detects, trims and, sets the command for the message
 * @method detectCommand 	- calls the removeKeyWord method and then calls the setCommand method
 * @method removeKeyWord 	- simply removes the key word from the message
 * @method setCommand		- sets the command for the message
 * 
 * @author a16tobfr
 */
public class CommandController {
	ChatMessage cm;
	
	public CommandController(ChatMessage cm) {
		this.cm = cm;
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
	
	private void removeKeyWord(String keyWord) {
		switch (keyWord) {
		case "/tell":
			cm.setMessage(cm.getMessage().replaceFirst("/tell", ""));
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
