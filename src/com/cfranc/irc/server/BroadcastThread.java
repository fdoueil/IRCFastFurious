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
			//clientTreadsMap.put(user, serverToClientThread);	
			// modifs du 03/11 : ajout de tous les users à la liste des users des clients
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
			System.out.println("sendMessage : "+"#"+sender.getLogin()+"#"+msg);
		}
	}
	
	public static void sendMessageSalon(Salon salon,User sender, String msg) {
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			//si user est dans le salon
			System.out.println("TAG:" + clientThread.getUser().getLogin());
			
			salon.allUser();
			if (salon.userExistSalon(clientThread.getUser().getLogin())) {
				// #Bill#C+MSG#Discussions#Message
				clientThread.post(IfClientServerProtocol.SEPARATOR +sender.getLogin()+ 
						IfClientServerProtocol.SEPARATOR +salon.getNomSalon() + 
						IfClientServerProtocol.SEPARATOR + msg + IfClientServerProtocol.SEPARATOR +IfClientServerProtocol.USER_MESSAGE_CHANNEL);
				//System.out.println("ENVOI:" + clientThread.getUser().getLogin());
			}				
			System.out.println("sendMessage : "+"#"+sender.getLogin()+"#"+msg);
		}
	}
	
	public static void sendQuitUser (User sender, String msg) {
		Collection<ServerToClientThread> clientTreads=clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator=clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			
			System.out.println("sendMessage : "+ msg+sender.getLogin());

			System.out.println(" sendQuitUser msg = " + msg);
			System.out.println(" sendQuitUser sender.getlogin() =" + sender.getLogin());
			System.out.println("sendMessage : "+ msg+sender.getLogin());
			
			clientThread.post(msg+sender.getLogin());
			
// 			System.out.println("sendMessage : "+ msg +);
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
