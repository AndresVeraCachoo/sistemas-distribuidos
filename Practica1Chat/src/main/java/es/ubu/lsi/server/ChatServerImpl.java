package es.ubu.lsi.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Chat server.
 * * -------------------------------------------------------------------
 * EXTRA FEATURES IMPLEMENTADAS EN ESTA PRÁCTICA:
 * 1. Multicast Directo: Envío a múltiples usuarios a la vez separando
 * los nombres por comas en el destinatario (ej. "Pedro,Maria").
 * 2. Control de Duplicados: El servidor rechaza y avisa a los clientes
 * que intentan conectarse con un nombre ya en uso.
 * 3. Comando WHO: Permite al cliente solicitar la lista de conectados.
 * 4. Comando AFK: Gestión de estados. Avisa automáticamente si un
 * usuario está ausente cuando se le intenta enviar un privado.
 * 5. Comando KICK: Expulsión forzada de clientes desde el servidor.
 * -------------------------------------------------------------------
 * * @author http://www.dreamincode.net
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Andres
 */
public class ChatServerImpl implements ChatServer {

	private static final int DEFAULT_PORT = 1500;
	private static int clientId;
	private List<ServerThreadForClient> clients;
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private int port;
	private boolean alive;
	private ServerSocket serverSocket; 

	public ChatServerImpl(int port) {
		this.port = port;
		clients = new ArrayList<>();
	}

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
				
				if (!socket.isClosed()) {
					clients.add(t);
					t.start();
				}
			}
			shutdown();
		} catch (IOException e) {
			show(sdf.format(new Date()) + " ServerSocket: " + e + "\n");
		}
	}

	@Override
	public synchronized void shutdown() {
		try {
			serverSocket.close();
			for (int i = 0; i < clients.size(); ++i) {
				clients.get(i).close();
			}
		} catch (Exception e) {
			show("Exception closing the server and clients: " + e);
		}
	}
	
	private void show(String event) {
		System.out.println(sdf.format(new Date()) + " " + event);
	}

	private synchronized boolean isUsernameTaken(String name) {
		for (ServerThreadForClient c : clients) {
			if (c.username != null && c.username.equalsIgnoreCase(name)) return true;
		}
		return false;
	}

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

	public synchronized void sendPrivateMessage(String targetUsers, ChatMessage message, ServerThreadForClient sender) {
		String time = sdf.format(new Date());
		String messageLf = time + " " + message.getMessage() + "\n";
		message.setMessage(messageLf);
		System.out.print(messageLf);
		
		List<String> targetsList = Arrays.asList(targetUsers.split(","));
		
		for (int i = clients.size(); --i >= 0;) {
			ServerThreadForClient ct = clients.get(i);
			if (targetsList.contains(ct.username) || ct == sender) {
				if (!ct.sendMessage(message)) {
					clients.remove(i);
				} else if (ct.isAfk && ct != sender) {
					sender.sendMessage(new ChatMessage(0, MessageType.MESSAGE, "Sistema: " + ct.username + " está AFK.\n"));
				}
			}
		}
	}
	
	private synchronized void processServerCommand(String cmd, ServerThreadForClient sender) {
		if (cmd.equals("WHO")) {
			StringBuilder sb = new StringBuilder("Conectados: ");
			for (ServerThreadForClient c : clients) {
				sb.append(c.username).append(c.isAfk ? "(AFK), " : ", ");
			}
			sender.sendMessage(new ChatMessage(0, MessageType.MESSAGE, "Sistema: " + sb.toString() + "\n"));
			
		} else if (cmd.equals("AFK")) {
			sender.isAfk = !sender.isAfk;
			String status = sender.isAfk ? "ausente" : "disponible";
			sender.sendMessage(new ChatMessage(0, MessageType.MESSAGE, "Sistema: Ahora estás " + status + ".\n"));
			
		} else if (cmd.startsWith("KICK|")) {
			kickUser(cmd.substring(5), sender);
		}
	}

	private synchronized void kickUser(String target, ServerThreadForClient sender) {
		for (int i = 0; i < clients.size(); i++) {
			ServerThreadForClient ct = clients.get(i);
			if (ct.username.equalsIgnoreCase(target)) {
				ct.sendMessage(new ChatMessage(0, MessageType.MESSAGE, "Sistema: Has sido expulsado del servidor.\n"));
				ct.close(); 
				clients.remove(i);
				broadcast(new ChatMessage(0, MessageType.MESSAGE, "Sistema: " + target + " fue expulsado por " + sender.username + "\n"));
				return;
			}
		}
		sender.sendMessage(new ChatMessage(0, MessageType.MESSAGE, "Sistema: Usuario no encontrado.\n"));
	}

	@Override
	public synchronized void remove(int id) {
		for (int i = 0; i < clients.size(); ++i) {
			if (clients.get(i).id == id) {
				clients.remove(i);
				return;
			}
		}
	}

	public static void main(String[] args) {
		int portNumber = DEFAULT_PORT;
		if (args.length == 1) {
			try { portNumber = Integer.parseInt(args[0]); } 
			catch (Exception e) { return; }
		}
		new ChatServerImpl(portNumber).startup();
	}

	class ServerThreadForClient extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		boolean isAfk = false;

		ServerThreadForClient(Socket socket) {
			id = ++clientId;
			this.socket = socket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
				
				if (isUsernameTaken(username)) {
					show("⚠️ Intento de conexión rechazado: el usuario '" + username + "' ya está en el servidor.");
					sOutput.writeInt(-1); 
					sOutput.flush();
					close();
					return;
				}
				
				sOutput.writeInt(id);
				sOutput.flush();
				show(username + " just connected.");	
				broadcast(new ChatMessage(id, MessageType.MESSAGE, username + " now connected"));
			} catch (IOException | ClassNotFoundException e) {
				close(); 
			}
		}

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

		private void routeChatMessage(ChatMessage msg) {
			String text = msg.getMessage();
			String target = msg.getReceiver();
			
			if ("server".equals(target)) {
				processServerCommand(text, this);
			} else if (target != null) {
				msg.setMessage(username + " (Privado): " + text);
				sendPrivateMessage(target, msg, this);
			} else {
				msg.setMessage(username + ": " + text);
				broadcast(msg);
			}
		}

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
		
		void close() {
			try {				
				if (sOutput != null) sOutput.close();
				if (sInput != null) sInput.close();
				if (socket != null && !socket.isClosed()) socket.close();
			} catch (Exception e) {}
		}
	}
}