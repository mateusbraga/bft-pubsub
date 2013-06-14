package pubsub;

import java.io.Serializable;

public class Evento extends Requisicao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String topico;
	public String msg;
	
	public String toString() {
		return "Topico: " + topico + " Mensagem: " + msg + ".";
	}
}
