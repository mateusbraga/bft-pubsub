package pubsub;

import java.io.Serializable;

public class ClientId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int processId;
	public String ip;
	public int porta;
	
	public String toString() {
		return "Processoid: " + processId + " Endereco:" + ip + ":" + porta;
	}
}
	