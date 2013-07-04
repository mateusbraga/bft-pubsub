package pubsub;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import navigators.smart.tom.ServiceProxy;


public class SubscriberClient {

	static ServiceProxy pubsubProxy;
	static Hashtable<Integer, Hashtable<Integer, Integer>> eventosSendoRecebidos = new Hashtable<Integer, Hashtable<Integer, Integer>>();
	static Hashtable<Integer, Set<Integer>> eventosRecebidos = new Hashtable<Integer, Set<Integer>>();

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 2) {
            System.out.println("Usage: java PublisherClient process_id topicos");
            System.exit(-1);
        }
		
		pubsubProxy =  new ServiceProxy(Integer.parseInt(args[0]));
		
		ServerSocket listenSocket = new ServerSocket(0);
		
		for (int i = 1; i < args.length; i++) {
			subscribe(args[i], listenSocket);
		}
		
        while(true) {
        	Socket clientSocket = listenSocket.accept();
        	new Connection(clientSocket);
        }
	}
	
	public static void subscribe(String topico, ServerSocket listenSocket) throws IOException {
		Cadastrar registrar = new Cadastrar();
		registrar.Topico = topico;
		registrar.clientId = new ClientId();
		registrar.clientId.ip = "localhost";
		registrar.clientId.porta = listenSocket.getLocalPort();

		registrar.tag = Requisicao.Tipo.Registro;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(registrar);
        
        pubsubProxy.invokeOrdered(out.toByteArray());
        
        System.out.println("Registrado em: " + topico);
	}
	
	public synchronized static void recebeuEvento(Evento evento) {
		
		if (eventosSendoRecebidos.containsKey(evento.clientId.processId)) {
			Hashtable<Integer, Integer> dict = eventosSendoRecebidos.get(evento.clientId.processId);
			Set<Integer> set = eventosRecebidos.get(evento.clientId.processId);
			
//			System.out.println("" + set);
			
			if(set.contains(evento.i)) {
				return;
			}
			
			if (dict.containsKey(evento.i)) {
				Integer n = dict.get(evento.i);
				int quorumOfMessages = pubsubProxy.getViewManager().getCurrentView().getN() - pubsubProxy.getViewManager().getCurrentView().getF();

				n++;
				dict.put(evento.i, n);
				
				if (n == quorumOfMessages) {
					set.add(evento.i);
					novoEvento(evento);
					
					// clean up eventosRecebidos
					if (set.size() > 50) {
						List<Integer> list = new LinkedList<Integer>();
						list.addAll(set);
						Collections.sort(list);
						
						Iterator<Integer> i = list.iterator();
						int menor = i.next();
						while (i.hasNext()) {
							int val = i.next();
							if (menor + 1 == val) {
								menor = val;
							} else {
								break;
							}
						}
						
						i = list.iterator();
						while (i.hasNext()) {
							int val = i.next();
							if (val < menor) {
								i.remove();
							} else {
								break;
							}
						}
						
						set.retainAll(list);
					}
				}
			} else {
				dict.put(evento.i, 1);
			}
		} else {
			Hashtable<Integer, Integer> dict = new Hashtable<Integer, Integer>();
			eventosSendoRecebidos.put(evento.clientId.processId, dict);
			dict.put(evento.i, 1);
			
			Set<Integer> set = new HashSet<Integer>();
			eventosRecebidos.put(evento.clientId.processId, set);
		}
	}
	
	public static void novoEvento(Evento evento) {
		System.out.println("Novo evento: \n" + evento + "\n-------------");
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
				
//				System.out.println("Evento lido:" + evento);
				SubscriberClient.recebeuEvento(evento);
			}
		} catch (EOFException e) {
			System.out.println("INFO: Uma conexao foi perdida.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}