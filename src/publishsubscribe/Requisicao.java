package publishsubscribe;


public class Requisicao {
	public Tipo tag;
	public ClientId clientId;
	
	
	
	public enum Tipo {
		NovoEvento, Registro, PegarEvento
	}
}
