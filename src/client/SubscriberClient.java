package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import publishsubscribe.ClientId;
import publishsubscribe.Evento;
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
		
		ServerSocket listenSocket = new ServerSocket(porta);
		
		ServiceProxy pubsubProxy = new ServiceProxy(Integer.parseInt(args[0]));
		
		Registrar registrar = new Registrar();
		registrar.Topico = args[1];
		registrar.clientId = new ClientId();
		registrar.clientId.ip = "localhost";
		registrar.clientId.porta = porta;

		registrar.tag = Requisicao.Tipo.Registro;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(registrar);
        
        System.out.println("Antes de Registrado!");
        pubsubProxy.invokeOrdered(out.toByteArray());
        
        System.out.println("Registrado!");
        
        
        while(true) {
        	Socket clientSocket = listenSocket.accept();
        	Connection c = new Connection(clientSocket);
        	
        }
	}
}

class Connection extends Thread{
	ObjectInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	
	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new ObjectInputStream(clientSocket.getInputStream()); 
			//out = new DataOutputStream(clientSocket.getOutputStream());
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
				
				System.out.println("Novo evento:" + evento);
				
					
			}
		}catch (EOFException e){
			System.out.println("EOF:"+ e.getMessage());
		}catch (IOException e) {
			System.out.println("IO:"+ e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}