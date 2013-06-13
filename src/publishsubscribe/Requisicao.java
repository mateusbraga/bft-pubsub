package publishsubscribe;

import java.io.Serializable;


public class Requisicao implements Serializable {
	public Tipo tag;
	public ClientId clientId;
	
	
	
	public enum Tipo {
		NovoEvento, Registro, Descadastrar
	}
//	public static class Tipo {
//		public static int NovoEvento = 0;
//		public static int Registro = 1;
//		public static int Descadastrar = 2;
//	}
}
