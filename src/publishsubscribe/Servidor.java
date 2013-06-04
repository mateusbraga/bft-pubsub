package publishsubscribe;

import bftsmart.statemanagment.ApplicationState;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.tom.server.Recoverable;

public class Servidor implements SingleExecutable, Recoverable {

	@Override
	public byte[] executeUnordered(byte[] arg0, MessageContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationState getState(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReplicaContext(ReplicaContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int setState(ApplicationState arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] executeOrdered(byte[] arg0, MessageContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
