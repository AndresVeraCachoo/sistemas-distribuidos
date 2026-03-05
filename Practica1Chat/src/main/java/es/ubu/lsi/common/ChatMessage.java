package es.ubu.lsi.common;

import java.io.*;

/**
 * Message in chat system.
 * * @author Raúl Marticorena
 * @author Joaquin P. Seco
 */
public class ChatMessage implements Serializable {

	/** Serial version UID. */
	private static final long serialVersionUID = 7467237896682458959L;

	/**
	 * Message type.
	 */
	public enum MessageType {
		/** Message. */
		MESSAGE,
		/** Shutdown server. */
		SHUTDOWN,		
		/** Logout client. */
		LOGOUT;		
	}
	
	/** Type. */
	private MessageType type;
	
	/** Text. */
	private String message;
	
	/** Client id. */
	private int id;
	
	/* ---------------------------------------------------------
	 * MODIFICACIÓN AUTORIZADA:
	 * Se añade el atributo 'receiver' para poder implementar
	 * la funcionalidad de mensajes privados directos entre
	 * usuarios sin tener que parsear el String del mensaje.
	 * --------------------------------------------------------- */
	private String receiver = null;
	
	/**
	 * Constructor.
	 * * @param id client id
	 * @param type type
	 * @param message message
	 */
	public ChatMessage(int id, MessageType type, String message) {
		this.setId(id);
		this.setType(type);
		this.setMessage(message);
	}
	
	/**
	 * Gets type.
	 * @return type
	 */
	public MessageType getType() {
		return type;
	}
	
	/**
	 * Sets type.
	 * @param type message type
	 */
	private void setType(MessageType type) {
		this.type = type;
	}
	
	/**
	 * Gets message.
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets message.
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets id.
	 * @return sender id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets sender id.
	 * @param id sender id
	 */
	private void setId(int id) {
		this.id = id;
	}

	/**
	 * Obtiene el destinatario del mensaje privado.
	 * @return nombre del destinatario o null si es para todos
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * Establece el destinatario para un mensaje privado.
	 * @param receiver nombre del destinatario
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}