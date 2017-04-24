package com.cfranc.irc.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.client.ClientToServerThread;
import com.cfranc.irc.server.ClientConnectThread;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;

public class SimpleChatClientApp {
	static String[] ConnectOptionNames = { "Connect" };
	static String ConnectTitle = "Connection Information";
	Socket socketClientServer;
	int serverPort;
	String serverName;
	String clientName;
	String clientPwd;

	private SimpleChatFrameClient frame;

	private HashMap<Integer, StyledDocument> hMapDocumentModel = new HashMap<Integer, StyledDocument>();
	private HashMap<Integer, DefaultListModel<String>> hMapListModel = new HashMap<Integer, DefaultListModel<String>>();

	private SalonLst hSalons;

	public static final String BOLD_ITALIC = "BoldItalic";
	public static final String GRAY_PLAIN = "Gray";

	public static DefaultListModel<String> createListModel() {
		DefaultListModel<String> res = new DefaultListModel<String>();
		return res;
	}

	public static DefaultStyledDocument createDefaultDocumentModel() {
		DefaultStyledDocument res = new DefaultStyledDocument();

		Style styleDefault = (Style) res.getStyle(StyleContext.DEFAULT_STYLE);

		res.addStyle(BOLD_ITALIC, styleDefault);
		Style styleBI = res.getStyle(BOLD_ITALIC);
		StyleConstants.setBold(styleBI, true);
		StyleConstants.setItalic(styleBI, true);
		StyleConstants.setForeground(styleBI, Color.black);

		res.addStyle(GRAY_PLAIN, styleDefault);
		Style styleGP = res.getStyle(GRAY_PLAIN);
		StyleConstants.setBold(styleGP, false);
		StyleConstants.setItalic(styleGP, false);
		StyleConstants.setForeground(styleGP, Color.lightGray);

		return res;
	}

	private static ClientToServerThread clientToServerThread;

	public SimpleChatClientApp() {
		this.hSalons = new SalonLst();
	}

	public HashMap<Integer, StyledDocument> gethMapDocumentModel() {
		return hMapDocumentModel;
	}

	public HashMap<Integer, DefaultListModel<String>> gethMapListModel() {
		return hMapListModel;
	}

	public void displayClient() {
		// hMapDocumentModel.put(0, documentModel);
		// hMapListModel.put(0, clientListModel);

		// Init GUI
		this.frame = new SimpleChatFrameClient(clientToServerThread, hMapListModel, hMapDocumentModel);
		this.frame.setTitle(
				this.frame.getTitle() + " : " + clientName + " connected to " + serverName + ":" + serverPort);
		((JFrame) this.frame).setVisible(true);
		this.frame.sethSalons(hSalons);
		
		this.frame.setUserName(clientName);
		
		this.frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowClosing");
				quitApp(SimpleChatClientApp.this);

			}

			@Override
			public void windowClosed(WindowEvent e) {
				quitApp(SimpleChatClientApp.this);
				System.out.println("windowClosed");
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void creerSalon(String userName, String salonName) {
		this.frame.creerSalon(userName, salonName);
	}

	public void creerSalonJoignable(String userName, String salonName) {
		this.frame.creerSalonJoignable(userName, salonName);
		Salon newSalon = new Salon(salonName, userName, false);
		newSalon.gethUsersLogin().add(userName);
		hSalons.getLstSalons().add(newSalon);
	}

	public void ajouterUserSalon(String userLogin, int indexSalon) {
		this.frame.ajouterUserSalon(userLogin, indexSalon);
	}

	public void hideClient() {

		// Init GUI
		((JFrame) this.frame).setVisible(false);
	}

	void displayConnectionDialog() {
		ConnectionPanel connectionPanel = new ConnectionPanel();
		if (JOptionPane.showOptionDialog(null, connectionPanel, ConnectTitle, JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, ConnectOptionNames, ConnectOptionNames[0]) == 0) {
			serverPort = Integer.parseInt(connectionPanel.getServerPortField().getText());
			serverName = connectionPanel.getServerField().getText();
			clientName = connectionPanel.getUserNameField().getText();
			clientPwd = connectionPanel.getPasswordField().getText();
		}
	}

	private void connectClient() {
		System.out.println("Establishing connection. Please wait ...");
		try {
			socketClientServer = new Socket(this.serverName, this.serverPort);
			// Start connection services
			clientToServerThread = new ClientToServerThread(this, hMapDocumentModel, hMapListModel, socketClientServer,
					clientName, clientPwd);
			clientToServerThread.start();

			System.out.println("Connected: " + socketClientServer);
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final SimpleChatClientApp app = new SimpleChatClientApp();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					app.displayConnectionDialog();

					app.connectClient();

					app.displayClient();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		Scanner sc = new Scanner(System.in);
		String line = "";
		while (!line.equals(".bye")) {
			line = sc.nextLine();
		}

		quitApp(app);
	}

	private static void quitApp(final SimpleChatClientApp app) {
		try {
			app.clientToServerThread.quitServer();
			app.socketClientServer.close();
			app.hideClient();
			System.out.println("SimpleChatClientApp : fermée");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SalonLst gethSalons() {
		return hSalons;
	}

}
