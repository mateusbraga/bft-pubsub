package pubsub;

import java.io.Serializable;


public class Requisicao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Tipo tag;
	public ClientId clientId;
	
	public enum Tipo {
		NovoEvento, Registro, Descadastrar
	}
}
