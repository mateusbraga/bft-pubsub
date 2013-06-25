package pubsub;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import navigators.smart.tom.MessageContext;
import navigators.smart.tom.ReplicaContext;
import navigators.smart.tom.ServiceReplica;
import navigators.smart.tom.server.Recoverable;
import navigators.smart.tom.server.SingleExecutable;


public class Servidor implements SingleExecutable, Recoverable {

	ServiceReplica replica = null;
    private ReplicaContext replicaContext;
    
    private HashMap<String, List<ClientId>> topicoParaInteressados = new HashMap<String, List<ClientId>>();
    private HashMap<ClientId, ObjectOutputStream> clientIdToObjectOutputStream = new HashMap<ClientId, ObjectOutputStream>();
    
    
    public Servidor(int id) {
    	replica = new ServiceReplica(id, this, this);
    }
	
	@Override
	public byte[] executeUnordered(byte[] arg0, MessageContext arg1) {
		return null;
	}

	
	@Override
	public byte[] executeOrdered(byte[] command, MessageContext msgContext) {
		System.out.println("Requisicao recebida");
		try {
			Requisicao req = (Requisicao) new ObjectInputStream(new ByteArrayInputStream(command)).readObject();
			
			if (req.tag == Requisicao.Tipo.NovoEvento) {
				System.out.println("Novo evento recebida");
				Evento evento = (Evento) req;
				
				if (topicoParaInteressados.containsKey(evento.topico)) {
					List<ClientId> clientesInteressados = topicoParaInteressados.get(evento.topico);
					for(ClientId clientId : clientesInteressados) {
						ObjectOutputStream objOutStream = clientIdToObjectOutputStream.get(clientId);
			            objOutStream.writeObject(evento);
			            objOutStream.flush();
					}
				}		
				
				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			} else if (req.tag == Requisicao.Tipo.Registro) {
				System.out.println("Novo pedido de Registro!");
				
				Registrar registrar = (Registrar) req;
				
				if (topicoParaInteressados.containsKey(registrar.Topico)) {
					topicoParaInteressados.get(registrar.Topico).add(registrar.clientId);
					
					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					clientIdToObjectOutputStream.put(registrar.clientId, new ObjectOutputStream(socket.getOutputStream()));
					
				} else {
					ArrayList<ClientId> list = new ArrayList<ClientId>();
					
					list.add(registrar.clientId);
					topicoParaInteressados.put(registrar.Topico, list);
					
					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					clientIdToObjectOutputStream.put(registrar.clientId, new ObjectOutputStream(socket.getOutputStream()));
				}
				
				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			} else if (req.tag == Requisicao.Tipo.Descadastrar) {
				System.out.println("Novo pedido para descadastrar recebido!");
				Descadastrar descadastrar = (Descadastrar) req;
				
				if (topicoParaInteressados.containsKey(descadastrar.Topico)) {
					topicoParaInteressados.get(descadastrar.Topico).remove(descadastrar.clientId);
					clientIdToObjectOutputStream.remove(descadastrar.clientId);
				}

				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			} else {
				System.out.println("Tipo de requisição não conhecido.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
            return new byte[0];
        }
		return null;
	}
	
    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CounterServer <processId>");
            System.exit(-1);
        }
        
        new Servidor(Integer.parseInt(args[0]));
    }

	@Override
	public byte[] getState() {
		System.out.println("executando getState()");
		try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(topicoParaInteressados);
            out.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
		} catch (IOException e) {
            System.out.println("Exception when trying to take a + " +
                            "snapshot of the application state" + e.getMessage());
            e.printStackTrace();
            return new byte[0];
    	}
	}

	@Override
	public void setState(byte[] state) {
		System.out.println("executando setState");
		ByteArrayInputStream bis = new ByteArrayInputStream(state);
        try {
                ObjectInput in = new ObjectInputStream(bis);
                topicoParaInteressados = (HashMap<String, List<ClientId>>) in.readObject();
                in.close();
                bis.close();
        } catch (ClassNotFoundException e) {
                System.out.print("Coudn't find Map: " + e.getMessage());
                e.printStackTrace();
        } catch (IOException e) {
                System.out.print("Exception installing the application state: " + e.getMessage());
                e.printStackTrace();
        }
		
	}
}
