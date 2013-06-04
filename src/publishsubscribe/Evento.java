package publishsubscribe;

import java.io.Serializable;

public class Evento implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String topico;
	public String msg;
}
