package es.ubu.lsi.client;

import java.net.*;
import java.io.*;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Client.
 * * @author http://www.dreamincode.net
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Andres
 */
public class ChatClientImpl implements ChatClient {
	
	/** Input stream. */
	private ObjectInputStream sInput;
	/** Output stream. */
	private ObjectOutputStream sOutput;
	/** Socket. */
	private Socket socket;

	/** Server name/IP. */
	private String server;
	/** User name. */
	private String username;
	/** Port. */
	private int port;
	
	/** Flag to keep running main thread. */
	private boolean carryOn = true;

	/** Id. */
	private int id;

	/** Lista de usuarios baneados (ignorados). */
	private Set<String> bannedUsers = new HashSet<>();

	/**
	 * Constructor.
	 * @param server server
	 * @param port port
	 * @param username user name
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/**
	 * Añade un usuario a la lista de baneados.
	 * @param userToBan nombre del usuario a ignorar
	 */
	public void banUser(String userToBan) {
		this.bannedUsers.add(userToBan);
	}

	/**
	 * Elimina un usuario de la lista de baneados.
	 * @param userToUnban nombre del usuario a volver a leer
	 */
	public void unbanUser(String userToUnban) {
		this.bannedUsers.remove(userToUnban);
	}

	/**
	 * Starts chat.
	 * @return true if everything goes right, false in other case
	 */
	@Override
	public boolean start() {
		try {
			socket = new Socket(server, port);
			display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		} catch (Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}			
		
		try {			
			sOutput.writeObject(username);	
			sOutput.flush();
			id = sInput.readInt();
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		new Thread(new ChatClientListener()).start();
		return true;
	}

	/**
	 * Displays messages.
	 * @param msg text to show in console
	 */
	private void display(String msg) {
		System.out.println(msg); 
	}

	/**
	 * Sends a message to the server.
	 * @param msg message
	 */
	@Override
	public synchronized void sendMessage(ChatMessage msg) {
		try {
			if (this.carryOn) {
				sOutput.writeObject(msg);
			}
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/**
	 * Disconnect client closing resources.
	 */
	@Override
	public void disconnect() {
		try {
			display("Trying to disconnect and close client with username " + username);
			if (sInput != null) sInput.close();
			if (sOutput != null) sOutput.close();
			if (socket != null && !socket.isClosed()) socket.close();
		} catch (Exception e) {
			display("Disconnect with error, closing resources, closed previously.");
		} finally {
			display("Bye!");
			carryOn = false;
		}
	}

	/**
	 * Procesa el input del usuario y lo enruta al tipo de mensaje adecuado.
	 * @param userMsg mensaje crudo del usuario
	 * @param client instancia del cliente para enviar
	 */
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
			// Mensaje del sistema sin destinatario
			client.sendMessage(new ChatMessage(id, MessageType.MESSAGE, username + " ha baneado a " + userToBan));
		} else if (userMsg.toLowerCase().startsWith("unban ")) {
			String userToUnban = userMsg.substring(6).trim();
			unbanUser(userToUnban);
			System.out.println("Has desbloqueado a " + userToUnban);
		} else if (userMsg.toLowerCase().startsWith("todos ")) {
			String text = userMsg.substring(6).trim();
			String wm = username + " patrocina el mensaje: " + text;
			client.sendMessage(new ChatMessage(id, MessageType.MESSAGE, wm));
		} else {
			sendPrivateMessage(userMsg, client);
		}
	}

	/**
	 * Envía un mensaje privado separando el destinatario del texto.
	 * @param userMsg mensaje crudo
	 * @param client cliente para envío
	 */
	private void sendPrivateMessage(String userMsg, ChatClient client) {
		String[] parts = userMsg.split(" ", 2);
		if (parts.length < 2) {
			System.out.println("❌ Formato incorrecto. Usa: 'Destinatario mensaje' o 'todos mensaje'");
		} else {
			String targetUser = parts[0];
			String wm = username + " patrocina el mensaje: " + parts[1];
			
			ChatMessage msg = new ChatMessage(id, MessageType.MESSAGE, wm);
			msg.setReceiver(targetUser); 
			client.sendMessage(msg);
		}
	}

	/**
	 * Starts the client.
	 * @param args arguments
	 */
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch (args.length) {
		case 3: serverAddress = args[2];
		case 2:
			try { portNumber = Integer.parseInt(args[1]); } 
			catch (Exception e) { System.out.println("Invalid port number."); return; }
		case 1: userName = args[0];
		case 0: break;
		default:
			System.err.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
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

	/**
	 * Client listener for messages from server.
	 */
	class ChatClientListener implements Runnable {
		
		/**
		 * Ejecuta el hilo de escucha para recibir mensajes.
		 */
		public void run() {
			while (carryOn) {
				try {
					ChatMessage msg = (ChatMessage) sInput.readObject();
					if (msg.getId() != id) {
						String fullText = msg.getMessage();
						String sender = extractSender(fullText);
						
						if (sender != null && bannedUsers.contains(sender)) {
							continue; 
						}
						
						System.out.print(fullText);
						System.out.print("> ");
					}
				} catch (IOException e) {
					display("Server has closed the connection. ");
					carryOn = false;
					break;
				} catch (ClassNotFoundException e2) {
					throw new RuntimeException("Wrong message type", e2);
				}
			} 
		} 
		
		/**
		 * Extrae el nombre de usuario de un mensaje del servidor.
		 * @param text mensaje completo
		 * @return el nombre de usuario
		 */
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