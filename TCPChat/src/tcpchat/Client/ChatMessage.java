package tcpchat.Client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * 
 * Class description:
 * This class is used when sending and receiving messages
 * 
 * Methods in this class:
 * @methods get/setMessage
 * @methods get/setName
 * @methods get/setTimeStamp
 * @methods get/setCommand
 * @methods get/setRecipient
 * @Override toString
 *
 * @author a16tobfr 
 * Project: TCPChat
 * Date: 17 feb. 2019
 */

@JsonRootName("client_message")
@JsonPropertyOrder(value = {"name", "command", "recipent", "message", "timestamp"})
public class ChatMessage{
	private String message;
	private String command;
	private String recipent;
	private String name;
	private long timeStamp;
	
	public ChatMessage() {
		this.message = "";
		this.name = "";
		this.timeStamp = 0;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getRecipent() {
		return recipent;
	}

	public void setRecipent(String recipent) {
		this.recipent = recipent;
	}
	
	@Override
	public String toString() {
		return "[" + name + "] sent-> " + message;
	}
}
