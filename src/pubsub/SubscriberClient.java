package pubsub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;

import bftsmart.tom.ServiceProxy;

public class SubscriberClient {

	static ServiceProxy pubsubProxy;
	static int porta = 7778;
	static Hashtable<Evento, Integer> eventosSendoRecebidos = new Hashtable<Evento, Integer>();

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 2) {
            System.out.println("Usage: java PublisherClient process_id topico");
            System.exit(-1);
        }
		
		pubsubProxy =  new ServiceProxy(Integer.parseInt(args[0]));
		
		ServerSocket listenSocket = new ServerSocket(porta);
		
		for (int i = 1; i < args.length; i++) {
			subscribe(args[1]);
		}
		System.out.println("Listen socket");
		
        while(true) {
        	Socket clientSocket = listenSocket.accept();
        	new Connection(clientSocket);
        }
	}
	
	public static void subscribe(String topico) throws IOException {
		Registrar registrar = new Registrar();
		registrar.Topico = topico;
		registrar.clientId = new ClientId();
		registrar.clientId.ip = "localhost";
		registrar.clientId.porta = porta;

		registrar.tag = Requisicao.Tipo.Registro;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(registrar);
        
        pubsubProxy.invokeOrdered(out.toByteArray());
        
        System.out.println("Registrado em: " + topico);
	}
	
	public synchronized static void recebeuEvento(Evento evento) {
		if (eventosSendoRecebidos.containsKey(evento)) {
			if (eventosSendoRecebidos.get(evento) == pubsubProxy.getViewManager().getCurrentView().getN() - pubsubProxy.getViewManager().getCurrentView().getF() - 1) {
				//TODO salvar mensagem mais recente recebida de cada publisher para ignorar as mensgens que chegar
				novoEvento(evento);
				eventosSendoRecebidos.remove(evento);
			} else {
				Integer n = eventosSendoRecebidos.get(evento);
				n++;
				eventosSendoRecebidos.put(evento, n);
			}
		} else {
			System.out.println("Else");
			eventosSendoRecebidos.put(evento, 1);
		}
		System.out.println("Evento recebido: "+evento + " numero de vezes: "+ eventosSendoRecebidos.get(evento));
		System.out.println("Evento hashcode: "+evento);
		System.out.println("tamanho da hashmap" + eventosSendoRecebidos.size());
	}
	
	public static void novoEvento(Evento evento) {
		System.out.println("Novo evento: " + evento);
	}
}

class Connection extends Thread{
	ObjectInputStream in;
	Socket clientSocket;
	
	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new ObjectInputStream(clientSocket.getInputStream()); 
			this.start();
		}catch (IOException e) {
			System.out.println("Connection:"+ e.getMessage());
		} 
	}
	public void run() {
		try {
			while(true) {
				Requisicao req = (Requisicao) in.readObject();
				Evento evento = (Evento) req;
				
				SubscriberClient.recebeuEvento(evento);
				System.out.println("Recebeu evento:" + evento);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}