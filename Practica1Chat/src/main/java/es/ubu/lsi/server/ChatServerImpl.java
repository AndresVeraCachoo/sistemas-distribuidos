package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Chat server. 
 * * @author http://www.dreamincode.net
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Andres
 */
public class ChatServerImpl implements ChatServer {

	/** Default port. */
	private static final int DEFAULT_PORT = 1500;
	
	/** Unique ID for each connection.*/	
	private static int clientId;
	
	/** Client list. */
	private List<ServerThreadForClient> clients;
	
	/** Util class to display time. */
	private static SimpleDateFormat sdf;
	
	/** Port number to listen for connection. */
	private int port;
	
	/** Flag will be turned of to stop the server. */
	private boolean alive;
	
	/** Server socket. */
	private ServerSocket serverSocket; 
	
	static {
		 sdf = new SimpleDateFormat("HH:mm:ss");
	}

	/**
	 * Constructor.
	 * @param port port to listen
	 */
	public ChatServerImpl(int port) {
		this.port = port;
		clients = new ArrayList<>();
	}

	/**
	 * Starts the server.
	 */
	@Override
	public void startup() {
		alive = true;
		try {
			serverSocket = new ServerSocket(port);
			while (alive) {
				show("Server waiting for Clients on port " + port + ".");
				Socket socket = serverSocket.accept();
				if (!alive) break;
				ServerThreadForClient t = new ServerThreadForClient(socket); 
				clients.add(t);
				t.start();
			}
			shutdown();
		} catch (IOException e) {
			show(sdf.format(new Date()) + " ServerSocket: " + e + "\n");
		}
	}

	/**
	 * Closes server.
	 */
	@Override
	public synchronized void shutdown() {
		try {
			serverSocket.close();
			for (int i = 0; i < clients.size(); ++i) {
				ServerThreadForClient tc = clients.get(i);
				try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
				} catch (IOException ioE) {
					System.err.printf("Error closing streams for client %d", i);
				}
			}
		} catch (Exception e) {
			show("Exception closing the server and clients: " + e);
		}
	}
	
	/**
	 * Shows an event.
	 * @param event event
	 */
	private void show(String event) {
		System.out.println(sdf.format(new Date()) + " " + event);
	}

	/**
	 * Broadcasts a message to all clients.
	 * @param message message
	 */
	@Override
	public synchronized void broadcast(ChatMessage message) {
		String time = sdf.format(new Date());
		String messageLf = time + " " + message.getMessage() + "\n";
		message.setMessage(messageLf);
		System.out.print(messageLf);
		
		for (int i = clients.size(); --i >= 0;) {
			ServerThreadForClient ct = clients.get(i);
			if (!ct.sendMessage(message)) {
				clients.remove(i);
				show("Disconnected Client " + ct.username + " removed.");
			}
		}
	}

	/**
	 * Envia un mensaje privado a un cliente específico y al emisor.
	 * @param targetUser nombre del destinatario
	 * @param message mensaje a enviar
	 * @param sender hilo del cliente que envía
	 */
	public synchronized void sendPrivateMessage(String targetUser, ChatMessage message, ServerThreadForClient sender) {
		String time = sdf.format(new Date());
		String messageLf = time + " " + message.getMessage() + "\n";
		message.setMessage(messageLf);
		System.out.print(messageLf);
		
		boolean found = false;
		for (int i = clients.size(); --i >= 0;) {
			ServerThreadForClient ct = clients.get(i);
			if (ct.username.equals(targetUser) || ct == sender) {
				if (!ct.sendMessage(message)) {
					clients.remove(i);
				}
				if (ct.username.equals(targetUser)) found = true;
			}
		}
		if (!found) {
			ChatMessage error = new ChatMessage(0, MessageType.MESSAGE, "Sistema: El usuario " + targetUser + " no existe.\n");
			sender.sendMessage(error);
		}
	}

	/**
	 * Removes a logout client.
	 * @param id client id
	 */
	@Override
	public synchronized void remove(int id) {
		for (int i = 0; i < clients.size(); ++i) {
			if (clients.get(i).id == id) {
				clients.remove(i);
				return;
			}
		}
	}

	/** * Runs the server.
	 * @param args arguments
	 */
	public static void main(String[] args) {
		int portNumber = DEFAULT_PORT;
		if (args.length == 1) {
			try {
				portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.err.println("Invalid port number.");
				return;
			}
		}
		ChatServer server = new ChatServerImpl(portNumber);
		server.startup();
	}

	/** * Thread for each client. 
	 */
	class ServerThreadForClient extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;

		/**
		 * Constructor. 
		 * @param socket socket
		 */
		ServerThreadForClient(Socket socket) {
			id = ++clientId;
			this.socket = socket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
				sOutput.writeInt(id);
				sOutput.flush();
				show(username + " just connected.");	
				broadcast(new ChatMessage(id, MessageType.MESSAGE, username + " now connected"));
			} catch (IOException | ClassNotFoundException e) {
				close(); 
			}
		}

		/**
		 * Run method.
		 */
		public void run() {
			boolean runningThread = true;
			while (runningThread) {
				try {
					ChatMessage chatMessage = (ChatMessage) sInput.readObject();
					runningThread = processMessage(chatMessage);
				} catch (Exception e) {
					break;
				}
			}
			remove(id);
			close();
			if (!alive) shutdown();
		}		

		/**
		 * Procesa el mensaje recibido del cliente según su tipo.
		 * @param msg mensaje recibido
		 * @return true si debe seguir escuchando
		 */
		private boolean processMessage(ChatMessage msg) {
			switch (msg.getType()) {
			case SHUTDOWN:
				alive = false;					
				return false;
			case LOGOUT:
				msg.setMessage(username + " leaving chat room!");
				broadcast(msg);
				return false;
			case MESSAGE:
				routeChatMessage(msg);
				return true;
			}
			return true;
		}

		/**
		 * Enruta un mensaje de chat comprobando si tiene destinatario.
		 * @param msg mensaje recibido
		 */
		private void routeChatMessage(ChatMessage msg) {
			String text = msg.getMessage();
			String target = msg.getReceiver();
			
			if (target != null) {
				msg.setMessage(username + " (Privado para " + target + "): " + text);
				sendPrivateMessage(target, msg, this);
			} else {
				msg.setMessage(username + ": " + text);
				broadcast(msg);
			}
		}

		/**
		 * Write a message to the client.
		 * @param msg message
		 * @return true if sent
		 */
		boolean sendMessage(ChatMessage msg) {
			if (!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
				return true;
			} catch (IOException e) {
				return false;
			}
		} 
		
		/**
		 * Close streams and socket.
		 */
		private void close() {
			try {				
				if (sOutput != null) sOutput.close();
				if (sInput != null) sInput.close();
				if (socket != null && !socket.isClosed()) socket.close();
			} catch (Exception e) {
				show("Closed streams");
			}
		}
	}
}