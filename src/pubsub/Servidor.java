package pubsub;


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bftsmart.statemanagment.ApplicationState;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;

public class Servidor implements SingleExecutable, Recoverable {

	ServiceReplica replica = null;
    private ReplicaContext replicaContext;
    
    private HashMap<String, List<ObjectOutputStream>> topicoParaInteressados = new HashMap<String, List<ObjectOutputStream>>();
    
    
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
					List<ObjectOutputStream> clientesInteressados = topicoParaInteressados.get(evento.topico);
					for(ObjectOutputStream objOutStream : clientesInteressados) {
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
					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					topicoParaInteressados.get(registrar.Topico).add(new ObjectOutputStream(socket.getOutputStream()));
				} else {
					ArrayList<ObjectOutputStream> list = new ArrayList<ObjectOutputStream>();

					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					list.add(new ObjectOutputStream(socket.getOutputStream()));
					topicoParaInteressados.put(registrar.Topico, list);					
				}
				
				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			} else if (req.tag == Requisicao.Tipo.Descadastrar) {
				System.out.println("Novo pedido para descadastrar recebido!");
				Descadastrar descadastrar = (Descadastrar) req;
				
				if (topicoParaInteressados.containsKey(descadastrar.Topico)) {
					Socket socket = new Socket (descadastrar.clientId.ip, descadastrar.clientId.porta);
					topicoParaInteressados.get(descadastrar.Topico).remove(socket);
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
	
	@Override
	public ApplicationState getState(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReplicaContext(ReplicaContext replicaContext) {
		this.replicaContext = replicaContext;
	}

	@Override
	public int setState(ApplicationState arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CounterServer <processId>");
            System.exit(-1);
        }
        
        new Servidor(Integer.parseInt(args[0]));
        
    }


}
