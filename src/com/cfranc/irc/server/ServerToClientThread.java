package com.cfranc.irc.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.cfranc.irc.IfClientServerProtocol;

public class ServerToClientThread extends Thread {
	private User user;

	public User getUser() {
		return user;
	}

	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	DefaultListModel<String> clientListModel; // new HRAJ
	private SalonLst serverSalon;

	public ServerToClientThread(User user, Socket socket) {
		super();
		this.user = user;
		this.socket = socket;
	}

	public ServerToClientThread(User user, Socket socket, DefaultListModel<String> clientListModel, SalonLst salonLst) {
		super();
		this.user = user;
		this.socket = socket;
		this.clientListModel = clientListModel;
		this.serverSalon = salonLst;
	}

	List<String> msgToPost = new ArrayList<String>();

	public synchronized void post(String msg) {
		msgToPost.add(msg);
	}

	public synchronized void postListSalon() {
		if (serverSalon.getLstSalons().size() > 1) {
			for (Salon salon : serverSalon.getLstSalons()) {
				if (salon.getNomSalon() != "G�n�ral") {
					msgToPost.add(IfClientServerProtocol.SEPARATOR + salon.getUserCreator()
							+ IfClientServerProtocol.SEPARATOR + salon.getNomSalon() + IfClientServerProtocol.SEPARATOR
							+ IfClientServerProtocol.OK_CHANNEL);
				}
			}
		}
	}

	private synchronized void doPost() {
		try {
			for (String msg : msgToPost) {
				streamOut.writeUTF(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
					if (streamIn.available() > 0) {
						// Parsing du message avec le s�parateur du protocole
						String line = streamIn.readUTF();
						String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
						String login = userMsg[1];
						String msg = userMsg[2];

						done = msg.equals(".bye");
						if (!done) {
							if (login.equals(user)) {
								System.err.println("ServerToClientThread::run(), login!=user" + login);
							}
							// Renvoi du message aux clients

							if (line.startsWith(IfClientServerProtocol.DEL)) {

								clientListModel.removeElement(user.getLogin());

								BroadcastThread.sendQuitUser(user, IfClientServerProtocol.DEL);
								BroadcastThread.removeClient(user);
								
								// CREATION SALON
							} else if (userMsg[2].startsWith(IfClientServerProtocol.CREATE_CHANNEL)) {
								// GESTION SALON PRIVE
								if (userMsg[3].equals(IfClientServerProtocol.CHANNEL_PRIVATE)) {
									String[] userSalonPrivate = userMsg[4].split("/");
									Salon salon = new Salon(userMsg[4], userSalonPrivate[0], true);
									salon.hUsersLogin.add(userSalonPrivate[0]);
									salon.hUsersLogin.add(userSalonPrivate[1]);
									serverSalon.getLstSalons().add(salon);
									BroadcastThread.sendMessageSalonPrivate(userMsg[4], userSalonPrivate[0], userSalonPrivate[1]);
								} else {
									if (login.equals(user.getLogin())) {

										Salon salon = new Salon(userMsg[3], user.getLogin(), false);

										// ajout le salon dans la liste des
										// salons
										if (!serverSalon.getLstSalons().contains(salon)) {
											serverSalon.getLstSalons().add(salon);
											// ajout le salon pour le user
											user.getSalons().add(salon);
											// Acquittement de la cr�ation du
											// salon
											BroadcastThread.sendMessage(user,
													salon.getNomSalon() + IfClientServerProtocol.SEPARATOR
															+ IfClientServerProtocol.OK_CHANNEL);
										}
										salon.hUsersLogin.add(user.getLogin());
									}
								}
								// UTILISATEUR DEMANDE A REJOINDRE UN SALON
							} else if (userMsg[2].startsWith(IfClientServerProtocol.USER_JOIN_CHANNEL)) {
								if (login.equals(user.getLogin())) {

									int indexSalon = 0;
									indexSalon = serverSalon.findSalonIndexByName(userMsg[3]);

									// ajout d utilisateur dans le salon
									serverSalon.get(indexSalon).hUsersLogin.add(userMsg[1]);

									// Acquittement de la cr�ation du salon
									BroadcastThread.sendMessage(user, userMsg[3] + IfClientServerProtocol.SEPARATOR
											+ IfClientServerProtocol.OK_JOIN_CHANNEL);
								}
								// UTILISATEUR POSTE UN MESSAGE SUR UN SALON
							} else if (userMsg[2].startsWith(IfClientServerProtocol.USER_MESSAGE_CHANNEL)) {
								int indexSalon = 0;
								indexSalon = serverSalon.findSalonIndexByName(userMsg[3]);
								Salon userSalon = serverSalon.get(indexSalon);
								BroadcastThread.sendMessageSalon(userSalon, user, userMsg[4]);

							} else {
								BroadcastThread.sendMessage(user, msg);
							}
						} else {

							for (Salon salon : serverSalon.getLstSalons()) {
								if (salon.getNomSalon() == "G�n�ral") {
									clientListModel.removeElement(user.getLogin());

									BroadcastThread.sendQuitUser(user, IfClientServerProtocol.DEL);
									BroadcastThread.removeClient(user);
								}
							}
						}
					} else {
						doPost();
					}
				} catch (IOException ioe) {
					done = true;
				}
			}
			close();
		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
