package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import publishsubscribe.ClientId;
import publishsubscribe.Registrar;
import publishsubscribe.Requisicao;
import bftsmart.tom.ServiceProxy;


public class SubscriberClient {

	static int porta = 7778;

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 2) {
            System.out.println("Usage: java PublisherClient process_id topico");
            System.exit(-1);
        }
		
		ServiceProxy pubsubProxy = new ServiceProxy(Integer.parseInt(args[0]));
		
		Registrar registrar = new Registrar();
		registrar.Topico = args[1];
		registrar.clientId = new ClientId();
		registrar.clientId.ip = "localhost";
		registrar.clientId.porta = porta;

		registrar.tag = Requisicao.Tipo.Registro;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(registrar);
        
        pubsubProxy.invokeOrdered(out.toByteArray());
        
        ServerSocket ss = new ServerSocket(porta);
        while(true) {
        	Socket newSocket = ss.accept();
        	OutputStream os = newSocket.getOutputStream();
        	
        }
        
        
	}
}