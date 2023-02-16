import java.io.Serializable;
import java.util.ArrayList;

public class ClientInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String user;
	String msg;
	Boolean send;
	ArrayList<String> toUsers;
	ArrayList<String> clients;
	
	public ClientInfo(String user, String msg, ArrayList<String> toUsers) {
		this.user = user;
		this.msg = msg;
		this.toUsers = toUsers;
	}
	
	public void updateMSG(String str) {
		msg = msg + "\nby "+user+"\n";
	}
}