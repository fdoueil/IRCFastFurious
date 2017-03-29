package com.cfranc.irc.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.cfranc.irc.IfClientServerProtocol;

public class ServerToClientThread extends Thread{
	private User user;
	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ServerToClientThread(User user, Socket socket) {
		super();
		this.user=user;
		this.socket = socket;
	}
	
	List<String> msgToPost=new ArrayList<String>();
	
	public synchronized void post(String msg){
		msgToPost.add(msg);
	}
	
	private synchronized void doPost(){
		try {
			for (String msg : msgToPost) {
					streamOut.writeUTF(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			msgToPost.clear();
		}
	}
	
	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(socket.getOutputStream());
	}
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}

	@Override
	public void run() {
		try {
			open();
			boolean done = false;
			while (!done) {
				try {
					if(streamIn.available()>0){
						// Parsing du message avec le séparateur du protocole
						String line = streamIn.readUTF();
						String[] userMsg=line.split(IfClientServerProtocol.SEPARATOR);
						String login=userMsg[1];
						String msg=userMsg[2];
						// Creer Salon
						if (userMsg[2].startsWith(IfClientServerProtocol.CREATE_CHANNEL)) {
							if(login.equals(user.getLogin())){
								Salon salon = new Salon(userMsg[3],false);
								user.getSalons().add(salon);
								// Acquittement de la création du salon
								BroadcastThread.sendMessage(user,salon.getNomSalon()+IfClientServerProtocol.SEPARATOR+IfClientServerProtocol.OK_CHANNEL);
							}
						}
						done = msg.equals(".bye");
						if(!done){
							if(login.equals(user)){
								System.err.println("ServerToClientThread::run(), login!=user"+login);
							}
							// Renvoi du message aux clients
							BroadcastThread.sendMessage(user,msg);
						}
					}
					else{
						doPost();
					}
				} 
				catch (IOException ioe) {
					done = true;
				}
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
