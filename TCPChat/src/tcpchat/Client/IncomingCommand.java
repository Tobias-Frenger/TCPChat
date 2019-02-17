package tcpchat.Client;

/**
 * Class description:
 * This class makes decisions based on what the incoming command is.
 * Note that all kind of commands does not need to be handled.
 * 
 * Methods in this class:
 * @method handleIncomingMessage
 * 
 * @author a16tobfr 
 * Project: TCPChat
 * Date: 17 feb. 2019
 */

public class IncomingCommand {
	private Client client;

	protected IncomingCommand(Client client) {
		this.client = client;
	}

	protected void handleIncomingMessage(ChatMessage cm) {
		String command = cm.getCommand();
		switch (command) {
		case "connect":
			client.setConnected(true);
		case "rename":
			client.getClientMessage().setName(cm.getRecipent());
			break;
		case "renameDuplicate":
			client.changeName(cm.getName());
			client.getGUI().setTitle("Chat client for " + cm.getName());
			client.getClientMessage().setName(cm.getName());
			break;
		case "notAlive":
			client.setConnected(false);
			break;
		default:
			break;
		}
	}
}
