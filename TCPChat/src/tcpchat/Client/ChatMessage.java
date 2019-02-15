package tcpchat.Client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("client_message")
@JsonPropertyOrder(value = {"name", "port", "ip", "command", "recipent", "message", "id", "timestamp"})

public class ChatMessage{
	private String message;
	private String command;
	private String recipent;
	private String ip;
	private String name;
	private String id;
	private long timeStamp;
	private int port;
	
	public ChatMessage() {
		this.message = "";
		this.ip = "";
		this.name = "";
		this.id = "";
		this.timeStamp = 0;
		this.port = 0;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString() {
		return "[" + name + "] sent-> " + message;
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
}
