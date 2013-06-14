package pubsub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


import bftsmart.tom.ServiceProxy;



public class PublisherClient {
	
	static int porta = 7777;

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 2) {
            System.out.println("Usage: java PublisherClient process_id topico");
            System.exit(-1);
        }
		
		ServiceProxy pubsubProxy = new ServiceProxy(Integer.parseInt(args[0]));
		
		Evento evento = new Evento();
		evento.topico = args[1];
		evento.clientId = new ClientId();
		evento.clientId.ip = "localhost";
		evento.clientId.porta = porta;

		evento.tag = Requisicao.Tipo.NovoEvento;
		
		for(int i = 1; ; i++) {			
			evento.msg = "Mensagem " + i;			
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(evento);
            
            pubsubProxy.invokeOrdered(out.toByteArray());	
            System.out.println("Enviado!");
            Thread.currentThread().sleep(3000);
		}		
	}
	
}
