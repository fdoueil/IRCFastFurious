package com.cfranc.irc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cfranc.irc.IfClientServerProtocol;

public class BroadcastThread extends Thread {
	
	public static HashMap<User, ServerToClientThread> clientTreadsMap=new HashMap<User, ServerToClientThread>();
	static{
		Collections.synchronizedMap(clientTreadsMap);
	}
	
	public static boolean addClient(User user, ServerToClientThread serverToClientThread){
		boolean res=true;
		if(clientTreadsMap.containsKey(user)){
			res=false;
		}
		else{

			for(Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {
				entry.getValue().post(IfClientServerProtocol.ADD+user.getLogin());
			}
			
			clientTreadsMap.put(user, serverToClientThread);
			
			for (Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {
				serverToClientThread.post(IfClientServerProtocol.ADD+entry.getKey().getLogin());
			} 
			serverToClientThread.postListSalon();
		}
		return res;
	}

	// Envoi du message à tous les thread ServerToClientThread
	public static void sendMessage(User sender, String msg){
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			clientThread.post("#"+sender.getLogin()+"#"+msg);			
		}
	}
	
	public static void sendMessageSalon(Salon salon,User sender, String msg) {
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();

			salon.allUser();
			if (salon.userExistSalon(clientThread.getUser().getLogin())) {
				// #Bill#C+MSG#Discussions#Message
				clientThread.post(IfClientServerProtocol.SEPARATOR +sender.getLogin()+ 
						IfClientServerProtocol.SEPARATOR +salon.getNomSalon() + 
						IfClientServerProtocol.SEPARATOR + msg + IfClientServerProtocol.SEPARATOR +IfClientServerProtocol.USER_MESSAGE_CHANNEL);
			}				
		}
	}
	
	public static void sendMessageSalonPrivate(String salon,String user0, String user1) {
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();

			if (clientThread.getUser().getLogin().equals(user0) || clientThread.getUser().getLogin().equals(user1)) {
				// #Bill#C+MSG#Discussions#Message
				String msg = IfClientServerProtocol.SEPARATOR +
						user0 + IfClientServerProtocol.SEPARATOR + user1 +
						IfClientServerProtocol.SEPARATOR + salon + 
						IfClientServerProtocol.SEPARATOR +
						IfClientServerProtocol.CREATE_CHANNEL+IfClientServerProtocol.CHANNEL_PRIVATE;
				clientThread.post(msg);
			}				

		}
	}
	public static void sendQuitUser (User sender, String msg) {
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
					
			clientThread.post(msg+sender.getLogin());
			
		}
		
	}
	
	public static void removeClient(User user){
		clientTreadsMap.remove(user);
	}
	
	public static boolean accept(User user){
		boolean res=true;
		if(clientTreadsMap.containsKey(user)){
			res= false;
		}
		return res;
	}
}
