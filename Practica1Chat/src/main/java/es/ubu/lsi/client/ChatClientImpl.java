package es.ubu.lsi.client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

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
	private ObjectInputStream sInput; // to read from the socket
	/** Output stream. */
	private ObjectOutputStream sOutput; // to write on the socket
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
	private Set<String> bannedUsers = new HashSet<String>();

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
	 * Constructor.
	 * * @param server server
	 * @param port port
	 * @param username user name
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/**
	 * Starts chat.
	 * * @return true if everything goes right, false in other case
	 */
	@Override
	public boolean start() {
		try {
			socket = new Socket(server, port);
			String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
			display(msg);
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		catch (Exception ec) {
			display("Error connectiong to server:" + ec);
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
	 * * @param msg text to show in console
	 */
	private void display(String msg) {
		System.out.println(msg); 
	}

	/**
	 * Sends a message to the server.
	 * * @param msg message
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
			if (sInput != null)  {
				sInput.close();
				sInput = null;
			}
			if (sOutput != null) {
				sOutput.close();
				sOutput = null;
			}
			if (socket != null && !socket.isClosed()) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			display("Disconnect with error, closing resources, closed previously.");
		}
		finally{
			display("Bye!");
			carryOn = false;
		}
	}

	/**
	 * Starts the client.
	 * * @param args arguments
	 */
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch (args.length) {
		case 3:
			serverAddress = args[2];
		case 2:
			try {
				portNumber = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				return;
			}
		case 1:
			userName = args[0];
		case 0:
			break;
		default:
			System.err.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		
		ChatClient client = new ChatClientImpl(serverAddress, portNumber, userName);
		if (!client.start()) {
			System.err.println("Error connecting server.");
			return;
		}

		ChatClientImpl clientChat = ((ChatClientImpl) client);
		try (Scanner scan = new Scanner(System.in)) {
			while (clientChat.carryOn) {
				System.out.print("> ");
				String userMsg = scan.nextLine();
				
				if (userMsg.equalsIgnoreCase(MessageType.LOGOUT.toString())) {
					client.sendMessage(new ChatMessage(clientChat.id, MessageType.LOGOUT, MessageType.LOGOUT.toString()));
					break;
				} 
				else if (userMsg.equalsIgnoreCase(MessageType.SHUTDOWN.toString())) {
					client.sendMessage(new ChatMessage(clientChat.id, MessageType.SHUTDOWN, MessageType.SHUTDOWN.toString()));
					break;
				} 
				// SISTEMA DE BANEO
				else if (userMsg.toLowerCase().startsWith("ban ")) {
					String userToBan = userMsg.substring(4).trim();
					clientChat.banUser(userToBan);
					// Avisa a todo el mundo que ha sido baneado
					String banMsg = clientChat.username + " ha baneado a " + userToBan;
					client.sendMessage(new ChatMessage(clientChat.id, MessageType.MESSAGE, banMsg));
				} 
				// SISTEMA DE DESBANEO
				else if (userMsg.toLowerCase().startsWith("unban ")) {
					String userToUnban = userMsg.substring(6).trim();
					clientChat.unbanUser(userToUnban);
					System.out.println("Has desbloqueado a " + userToUnban);
				} 
				// MENSAJE NORMAL CON SELLO DE AUTORÍA (REQUISITO PDF)
				else { 
					String watermarkMsg = clientChat.username + " patrocina el mensaje: " + userMsg;
					client.sendMessage(new ChatMessage(clientChat.id, MessageType.MESSAGE, watermarkMsg));
				}
				System.out.println();
			}
		}
		client.disconnect();		
	}

	/**
	 * Client listener for messages from server.
	 */
	class ChatClientListener implements Runnable {
		
		/**
		 * Ejecuta el hilo de escucha para recibir mensajes del servidor.
		 */
		public void run() {
			while (carryOn) {
				try {
					ChatMessage msg = (ChatMessage) sInput.readObject();
					if (msg.getId() != id) {
						String fullText = msg.getMessage();
						String sender = extractSender(fullText);
						
						// Si el usuario está en la lista negra, saltamos y no imprimimos
						if (sender != null && bannedUsers.contains(sender)) {
							continue; 
						}
						
						System.out.println(fullText);
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
		 * Extrae el nombre de usuario de un mensaje formateado por el servidor.
		 * @param text mensaje completo recibido
		 * @return el nombre de usuario, o null si es un mensaje del sistema
		 */
		private String extractSender(String text) {
			if (text == null) return null;
			String[] parts = text.split(" ", 3);
			// El servidor añade "HH:mm:ss Usuario: Texto", buscamos la posición 1
			if (parts.length >= 2 && parts[1].endsWith(":")) {
				return parts[1].substring(0, parts[1].length() - 1);
			}
			return null;
		}
	} 

}