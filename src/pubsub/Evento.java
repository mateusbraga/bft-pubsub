package pubsub;

import java.io.Serializable;

public class Evento extends Requisicao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int i;
	public String topico;
	public String msg;
	
	public String toString() {
		return "Topico: " + topico + "\nMensagem: " + msg + "\nId:" + i + ".";
	}
	
	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof Evento) {
			Evento that = (Evento) other;
			result = (this.topico.equals(that.topico) && this.msg.equals(that.msg));
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return (41 * (41 + topico.hashCode()) + msg.hashCode());
	}
}
