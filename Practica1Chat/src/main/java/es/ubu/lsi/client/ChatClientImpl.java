package es.ubu.lsi.client;

import java.net.*;
import java.io.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Client.
 * @author http://www.dreamincode.net
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Andres
 */
public class ChatClientImpl implements ChatClient {
	
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	private String server;
	private String username;
	private int port;
	private boolean carryOn = true;
	private int id;
	private Set<String> bannedUsers = new HashSet<>();

	public ChatClientImpl(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	public void banUser(String userToBan) { this.bannedUsers.add(userToBan); }
	public void unbanUser(String userToUnban) { this.bannedUsers.remove(userToUnban); }

	@Override
	public boolean start() {
		try {
			socket = new Socket(server, port);
			display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}			
		
		try {			
			sOutput.writeObject(username);	
			sOutput.flush();
			id = sInput.readInt();
			
			// Si el ID es -1, significa que el nombre ya está en uso
			if (id == -1) {
				display("❌ Error: El nombre de usuario '" + username + "' ya está en uso.");
				disconnect();
				return false;
			}
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		new Thread(new ChatClientListener()).start();
		return true;
	}

	private void display(String msg) { System.out.println(msg); }

	@Override
	public synchronized void sendMessage(ChatMessage msg) {
		try {
			if (this.carryOn) sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	@Override
	public void disconnect() {
		try {
			if (sInput != null) sInput.close();
			if (sOutput != null) sOutput.close();
			if (socket != null && !socket.isClosed()) socket.close();
		} catch (Exception e) {} 
		finally {
			display("Bye!");
			carryOn = false;
		}
	}

	private void processUserInput(String userMsg, ChatClient client) {
		if (userMsg.equalsIgnoreCase(MessageType.LOGOUT.toString())) {
			client.sendMessage(new ChatMessage(id, MessageType.LOGOUT, ""));
			this.carryOn = false;
		} else if (userMsg.equalsIgnoreCase(MessageType.SHUTDOWN.toString())) {
			client.sendMessage(new ChatMessage(id, MessageType.SHUTDOWN, ""));
			this.carryOn = false;
		} else if (userMsg.toLowerCase().startsWith("ban ")) {
			String userToBan = userMsg.substring(4).trim();
			banUser(userToBan);
			client.sendMessage(new ChatMessage(id, MessageType.MESSAGE, username + " ha baneado a " + userToBan));
		} else if (userMsg.toLowerCase().startsWith("unban ")) {
			String userToUnban = userMsg.substring(6).trim();
			unbanUser(userToUnban);
			System.out.println("Has desbloqueado a " + userToUnban);
		} else {
			processExtendedCommands(userMsg, client);
		}
	}

	private void processExtendedCommands(String userMsg, ChatClient client) {
		if (userMsg.equalsIgnoreCase("who")) {
			ChatMessage msg = new ChatMessage(id, MessageType.MESSAGE, "WHO");
			msg.setReceiver("server");
			client.sendMessage(msg);
		} else if (userMsg.equalsIgnoreCase("afk")) {
			ChatMessage msg = new ChatMessage(id, MessageType.MESSAGE, "AFK");
			msg.setReceiver("server");
			client.sendMessage(msg);
		} else if (userMsg.toLowerCase().startsWith("kick ")) {
			String target = userMsg.substring(5).trim();
			ChatMessage msg = new ChatMessage(id, MessageType.MESSAGE, "KICK|" + target);
			msg.setReceiver("server");
			client.sendMessage(msg);
		} else if (userMsg.toLowerCase().startsWith("todos ")) {
			String text = userMsg.substring(6).trim();
			String wm = username + " patrocina el mensaje: " + text;
			client.sendMessage(new ChatMessage(id, MessageType.MESSAGE, wm));
		} else {
			sendPrivateMessage(userMsg, client);
		}
	}

	private void sendPrivateMessage(String userMsg, ChatClient client) {
		String[] parts = userMsg.split(" ", 2);
		if (parts.length < 2) {
			System.out.println("❌ Formato incorrecto. Usa: 'Usuario1,Usuario2 mensaje' o 'todos mensaje'");
		} else {
			String wm = username + " patrocina el mensaje: " + parts[1];
			ChatMessage msg = new ChatMessage(id, MessageType.MESSAGE, wm);
			msg.setReceiver(parts[0]); 
			client.sendMessage(msg);
		}
	}

	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch (args.length) {
		case 3: serverAddress = args[2];
		case 2:
			try { portNumber = Integer.parseInt(args[1]); } 
			catch (Exception e) { System.out.println("Invalid port."); return; }
		case 1: userName = args[0];
		case 0: break;
		default:
			System.err.println("Usage: java Client [username] [port] [server]");
			return;
		}
		
		ChatClient client = new ChatClientImpl(serverAddress, portNumber, userName);
		if (!client.start()) return;

		ChatClientImpl clientChat = ((ChatClientImpl) client);
		try (Scanner scan = new Scanner(System.in)) {
			while (clientChat.carryOn) {
				System.out.print("> ");
				clientChat.processUserInput(scan.nextLine(), client);
			}
		}
		client.disconnect();		
	}

	class ChatClientListener implements Runnable {
		public void run() {
			while (carryOn) {
				try {
					ChatMessage msg = (ChatMessage) sInput.readObject();
					if (msg.getId() != id) {
						String sender = extractSender(msg.getMessage());
						if (sender != null && bannedUsers.contains(sender)) continue; 
						System.out.print(msg.getMessage());
						System.out.print("> ");
					}
				} catch (IOException | ClassNotFoundException e) {
					display("Server has closed the connection.");
					carryOn = false;
					break;
				}
			} 
		} 
		
		private String extractSender(String text) {
			if (text == null) return null;
			String[] parts = text.split(" ", 3);
			if (parts.length >= 2 && parts[1].endsWith(":")) {
				return parts[1].substring(0, parts[1].length() - 1);
			}
			return null;
		}
	} 
}