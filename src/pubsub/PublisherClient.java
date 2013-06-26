package pubsub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import navigators.smart.tom.ServiceProxy;

public class PublisherClient {
	
	static int contador = 0;

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 2) {
            System.out.println("Usage: java PublisherClient process_id topico");
            System.exit(-1);
        }
		
		int processId = Integer.parseInt(args[0]);
		ServiceProxy pubsubProxy = new ServiceProxy(processId);
		
		Evento evento = new Evento();
		evento.topico = args[1];
		evento.clientId = new ClientId();
		evento.clientId.ip = "localhost";
		evento.clientId.porta = 0;
		evento.clientId.processId = processId;
		

		evento.tag = Requisicao.Tipo.NovoEvento;
		
		for(int i = 1; ; i++) {			
			evento.msg = "Mensagem " + i;
			evento.i = contador++;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(evento);
            
            pubsubProxy.invokeOrdered(out.toByteArray());	
            System.out.println("Enviado!");
            Thread.currentThread().sleep(3000);
		}		
	}
	
}
