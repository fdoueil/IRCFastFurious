package com.cfranc.irc.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.IfClientServerProtocol;
import com.cfranc.irc.server.BroadcastThread;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.ui.SimpleChatClientApp;

public class ClientToServerThread extends Thread implements IfSenderModel {
	private Socket socket = null;
	private DataOutputStream streamOut = null;
	private DataInputStream streamIn = null;
	private BufferedReader console = null;
	String login, pwd;
	DefaultListModel<String> clientListModel;
	StyledDocument documentModel;
	SimpleChatClientApp controleur;
	private HashMap<Integer, StyledDocument> hMapDocumentModel;
	private HashMap<Integer, DefaultListModel<String>> hMapListModel;

	public ClientToServerThread(SimpleChatClientApp controleur, HashMap<Integer, StyledDocument> hMapDocumentModel,
			HashMap<Integer, DefaultListModel<String>> hMapListModel, Socket socket, String login, String pwd) {
		super();
		// this.documentModel = document;
		// this.clientListModel = clientListModel;
		this.hMapDocumentModel = hMapDocumentModel;
		this.hMapListModel = hMapListModel;
		this.socket = socket;
		this.login = login;
		this.pwd = pwd;
		this.controleur = controleur;
	}

	public void open() throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
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

	public void receiveMessage(String user, String line) {
		Style styleBI = ((StyledDocument) hMapDocumentModel.get(0)).getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = ((StyledDocument) hMapDocumentModel.get(0)).getStyle(SimpleChatClientApp.GRAY_PLAIN);
		receiveMessage(0,user, line, styleBI, styleGP);
	}
	
	public void receiveMessageSalon(String salon, String user, String line){
		
		int numSalon=0;
		
		numSalon=controleur.gethSalons().findSalonIndexByName(salon);
		Style styleBI = ((StyledDocument) hMapDocumentModel.get(numSalon)).getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = ((StyledDocument) hMapDocumentModel.get(numSalon)).getStyle(SimpleChatClientApp.GRAY_PLAIN);
		
		receiveMessage(numSalon,user, line, styleBI, styleGP);
	}
	
	public void receiveMessage(int numOnglet,String user, String line, Style styleBI, Style styleGP) {
		try {
			hMapDocumentModel.get(numOnglet).insertString(hMapDocumentModel.get(numOnglet).getLength(), user + " : ", styleBI);
			hMapDocumentModel.get(numOnglet).insertString(hMapDocumentModel.get(numOnglet).getLength(), line + "\n", styleGP);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	


	void readMsg() throws IOException {
		String line = streamIn.readUTF();
		System.out.println("RECU->"+line);

		if (line.startsWith(IfClientServerProtocol.ADD)) {
			String newUser = line.substring(IfClientServerProtocol.ADD.length());
			if (!hMapListModel.get(0).contains(newUser)) {
				hMapListModel.get(0).addElement(newUser);
				receiveMessage(newUser, " entre dans le salon...");
			}
		} else if (line.startsWith(IfClientServerProtocol.DEL)) {
			String delUser = line.substring(IfClientServerProtocol.DEL.length());
			if (hMapListModel.get(0).contains(delUser)) {
				hMapListModel.get(0).removeElement(delUser);
				receiveMessage(delUser, " quitte le salon !");
			}
		} else if (line.endsWith(IfClientServerProtocol.OK_CHANNEL)) {
	
			String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
			if (userMsg[1].equals(this.login)) {
				Salon newSalon=new Salon(userMsg[2], userMsg[1], false);
				newSalon.gethUsersLogin().add(userMsg[1]);
				controleur.gethSalons().getLstSalons().add(newSalon);
				controleur.creerSalon(userMsg[1], userMsg[2],controleur.gethSalons().findSalonIndexByName(userMsg[2]));
			} else {	
				controleur.creerSalonJoignable(userMsg[1], userMsg[2]);
			}
			receiveMessage(userMsg[1], "a crée le salon " + userMsg[2]);
			
		} else if (line.endsWith(IfClientServerProtocol.OK_JOIN_CHANNEL)) {
			String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
			receiveMessage(userMsg[1], " a rejoint le salon " + userMsg[2]);

			int indexSalon= controleur.gethSalons().findSalonIndexByName(userMsg[2]);
			if (userMsg[1].equals(this.login)) {
				controleur.creerSalon(userMsg[1], userMsg[2], indexSalon);
				Salon salon = controleur.gethSalons().get(indexSalon);
				salon.gethUsersLogin().add(userMsg[1]);
				controleur.ajouterUserSalon(salon.getUserCreator(),indexSalon);
			} else {
				controleur.ajouterUserSalon(userMsg[1],indexSalon);
			}
		} else if (line.endsWith(IfClientServerProtocol.USER_MESSAGE_CHANNEL)){ 
			String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
			receiveMessageSalon(userMsg[2],userMsg[1],userMsg[3]);
		}else if (line.endsWith(IfClientServerProtocol.CREATE_CHANNEL+IfClientServerProtocol.CHANNEL_PRIVATE)) {
			String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
			controleur.gethSalons().getLstSalons().add(new Salon(userMsg[3],userMsg[1],true));
			int indexSalon= controleur.gethSalons().findSalonIndexByName(userMsg[3]);
			controleur.creerSalon(userMsg[1], userMsg[3], indexSalon);
			controleur.ajouterUserSalon(userMsg[2],indexSalon);
		} else {
			String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
			String user = userMsg[1];
			receiveMessage(user, userMsg[2]);
		}
	}

	String msgToSend = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfranc.irc.client.IfSenderModel#setMsgToSend(java.lang.String)
	 */
	@Override
	public void setMsgToSend(String msgToSend) {
		this.msgToSend = msgToSend;
	}

	private boolean sendMsg() throws IOException {
		boolean res = false;
		if (msgToSend != null) {
			streamOut.writeUTF("#" + login + "#" + msgToSend);
			msgToSend = null;
			streamOut.flush();
			res = true;
		}
		return res;
	}

	public void quitServer() throws IOException {
		streamOut.writeUTF(IfClientServerProtocol.DEL + login);
		streamOut.flush();
		done = true;
	}

	boolean done;

	@Override
	public void run() {
		try {
			open();
			done = !authentification();
			while (!done) {
				try {
					if (streamIn.available() > 0) {
						System.out.println("coté client streamIn.available >0 ");
						readMsg();
					}

					if (!sendMsg()) {
						Thread.sleep(100);
					}

				} catch (IOException | InterruptedException ioe) {
					ioe.printStackTrace();
					done = true;
				}
			}

			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean authentification() {
		boolean res = false;
		String loginPwdQ;
		try {
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			loginPwdQ = streamIn.readUTF();
			if (loginPwdQ.equals(IfClientServerProtocol.LOGIN_PWD)) {
				streamOut.writeUTF(
						IfClientServerProtocol.SEPARATOR + this.login + IfClientServerProtocol.SEPARATOR + this.pwd);
			}
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			String acq = streamIn.readUTF();
			if (acq.equals(IfClientServerProtocol.OK)) {
				res = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
