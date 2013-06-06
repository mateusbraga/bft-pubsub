package publishsubscribe;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;


import bftsmart.statemanagment.ApplicationState;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.tom.server.Recoverable;

public class Servidor implements SingleExecutable, Recoverable {

	ServiceReplica replica = null;
    private ReplicaContext replicaContext;
    
    private HashMap<String, List<Socket>> topicoParaInteressados = new HashMap<String, List<Socket>>();
    
    
    public Servidor(int id) {
    	replica = new ServiceReplica(id, this, this);
    }
	
	@Override
	public byte[] executeUnordered(byte[] arg0, MessageContext arg1) {
		return null;
	}

	@Override
	public byte[] executeOrdered(byte[] command, MessageContext msgContext) {
		try {
			Requisicao req = (Requisicao) new ObjectInputStream(new ByteArrayInputStream(command)).readObject();
			
			if (req.tag == Requisicao.Tipo.NovoEvento) {
				Evento evento = (Evento) req;
				
				if (topicoParaInteressados.containsKey(evento.topico)) {
					List<Socket> clientesInteressados = topicoParaInteressados.get(evento.topico);
					for(Socket clientSocket : clientesInteressados) {
						OutputStream outputStream = clientSocket.getOutputStream();
						
						ByteArrayOutputStream out = new ByteArrayOutputStream();
			            new ObjectOutputStream(out).writeObject(evento);
			            outputStream.write(out.toByteArray());
			            outputStream.flush();
					}
				}		
				
				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			} else if (req.tag == Requisicao.Tipo.Registro) {
				Registrar registrar = (Registrar) req;
				
				if (topicoParaInteressados.containsKey(registrar.Topico)) {
					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					topicoParaInteressados.get(registrar.Topico).add(socket);
				} else {
					ArrayList<Socket> list = new ArrayList<Socket>();

					Socket socket = new Socket (registrar.clientId.ip, registrar.clientId.porta);
					list.add(socket);
					topicoParaInteressados.put(registrar.Topico, list);					
				}
				
				String resposta = "Processado com sucesso!";
	            return resposta.getBytes();
			}
		} catch (Exception ex) {
            System.err.println("Invalid request received!");
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


}
