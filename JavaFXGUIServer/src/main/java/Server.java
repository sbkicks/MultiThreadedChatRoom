import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<String> list = new ArrayList<String>();
	TheServer server;
	ClientInfo info;
	
	private Consumer<Serializable> callback;
	
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			String msg;
			String user;
			ArrayList<String> toUsers;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(ClientInfo info) throws IOException {
				for(int i = 0; i < clients.size(); i++) {
					clients.get(i).out.writeObject(info);
					clients.get(i).out.reset();
				}
			}
			
			public void updateClients2(ClientInfo info) throws IOException {
				for(int i = 0; i < clients.size(); i++) {
					if (toUsers.contains(clients.get(i).user)) {
						clients.get(i).out.writeObject(info);
						clients.get(i).out.reset();
					}
				}
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
					
				 while(true) {
					    try {

					    	info = (ClientInfo)in.readObject();
					    	msg = info.msg;
					    	
					    	if (msg.isEmpty()) {
					    		user = info.user;
					    		list.add(user);
					    		info.clients = list;
					    		info.send = true;
					    		updateClients(info);
					    	} else {
					    		info.clients = list;
					    		toUsers = info.toUsers;
					    		info.updateMSG(user);
					    		info.send = false;
					    		updateClients2(info);
					    	}
					    	callback.accept("Client #"+count+" sent: ");
					    	
					    }
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	list.remove(user);
					    	ClientInfo info = new ClientInfo("", "", new ArrayList<String>());
					    	info.clients = list;
				    		info.send = true;
					    	try {
								updateClients(info);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
