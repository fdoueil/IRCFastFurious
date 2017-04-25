package com.cfranc.irc.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.IfClientServerProtocol;
import com.cfranc.irc.client.IfSenderModel;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;

public class SimpleChatFrameClient extends JFrame {

	IfSenderModel sender;
	private String senderName;

	private JPanel contentPane;
	private JTextField textField;
	private JLabel lblSender;
	private final ResourceAction sendAction = new SendAction();
	private final ResourceAction lockAction = new LockAction();
	private JTabbedPane tabbedPane;

	private JComboBox cbSalonJoignable;
	private HashMap<Integer, StyledDocument> hMapDocumentModel;
	private HashMap<Integer, DefaultListModel<String>> hMapListModel;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private SalonLst hSalons;

	public SalonLst gethSalons() {
		return hSalons;
	}

	public void sethSalons(SalonLst hSalons) {
		this.hSalons = hSalons;
	}

	private boolean isScrollLocked = true;

	JFrame frmEdd = new JFrame();

	/**
	 * Launch the application.
	 * 
	 * @throws BadLocationException
	 */
	public static void main(String[] args) throws BadLocationException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleChatFrameClient frame = new SimpleChatFrameClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Scanner sc = new Scanner(System.in);
		String line = ""; //$NON-NLS-1$
		while (!line.equals(".bye")) { //$NON-NLS-1$
			line = sc.nextLine();
		}
	}

	public void sendMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			// documentModel.insertString(documentModel.getLength(), user+" : ",
			// styleBI); //$NON-NLS-1$
			// documentModel.insertString(documentModel.getLength(), line+"\n",
			// styleGP); //$NON-NLS-1$
			hMapDocumentModel.get(0).insertString(hMapDocumentModel.get(0).getLength(), user + " : ", styleBI); //$NON-NLS-1$
			hMapDocumentModel.get(0).insertString(hMapDocumentModel.get(0).getLength(), line + "\n", styleGP); //$NON-NLS-1$

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void sendMessage() {
		// si onglet Général
		if (tabbedPane.getSelectedIndex() == 0) {
			sender.setMsgToSend(textField.getText());
		} else {
			sender.setMsgToSend(IfClientServerProtocol.USER_MESSAGE_CHANNEL + IfClientServerProtocol.SEPARATOR
					+ tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()) + IfClientServerProtocol.SEPARATOR
					+ textField.getText());
		}
	}

	public SimpleChatFrameClient() {
		// UNIQUEMENT POUR LE DESIGNER
		this(null, new HashMap<Integer, DefaultListModel<String>>(), new HashMap<Integer, StyledDocument>());
	}

	private void ajoutSalon(Integer idSalon) {
		DefaultListModel<String> value = SimpleChatClientApp.createListModel();
		this.hMapListModel.put(idSalon, value);
		StyledDocument value1 = SimpleChatClientApp.createDefaultDocumentModel();
		this.hMapDocumentModel.put(idSalon, value1);
	}

	/**
	 * Create the frame.
	 */
	public SimpleChatFrameClient(IfSenderModel sender, HashMap<Integer, DefaultListModel<String>> hMapListModel,
			HashMap<Integer, StyledDocument> hMapDocumentModel) {
		this.sender = sender;

		// Initialisation de la reference local aux models et création du salon
		// ZERO
		this.hMapListModel = hMapListModel;
		this.hMapDocumentModel = hMapDocumentModel;

		ajoutSalon(0);

		setTitle(Messages.getString("SimpleChatFrameClient.4")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu(Messages.getString("SimpleChatFrameClient.5")); //$NON-NLS-1$
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmEnregistrerSous = new JMenuItem(Messages.getString("SimpleChatFrameClient.6")); //$NON-NLS-1$
		mnFile.add(mntmEnregistrerSous);

		JMenu mnOutils = new JMenu(Messages.getString("SimpleChatFrameClient.7")); //$NON-NLS-1$
		mnOutils.setMnemonic('O');
		menuBar.add(mnOutils);

		JMenuItem mntmEnvoyer = new JMenuItem(Messages.getString("SimpleChatFrameClient.8")); //$NON-NLS-1$
		mntmEnvoyer.setAction(sendAction);
		mnOutils.add(mntmEnvoyer);

		JSeparator separator = new JSeparator();
		mnOutils.add(separator);
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem(lockAction);
		mnOutils.add(chckbxmntmNewCheckItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel_1.add(panel);

		lblSender = new JLabel("?"); //$NON-NLS-1$
		lblSender.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSender.setHorizontalTextPosition(SwingConstants.CENTER);
		lblSender.setPreferredSize(new Dimension(100, 14));
		lblSender.setMinimumSize(new Dimension(100, 14));

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				Messages.getString("SimpleChatFrameClient.12")); //$NON-NLS-1$
		textField.getActionMap().put(Messages.getString("SimpleChatFrameClient.13"), sendAction); //$NON-NLS-1$

		JButton btnSend = new JButton(sendAction);
		btnSend.setMnemonic(KeyEvent.VK_ENTER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(lblSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addGap(10)
				.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(lblSender, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		panel.setLayout(gl_panel);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		JButton button = toolBar.add(sendAction);

		JButton btnNouveauSalon = new JButton(Messages.getString("SimpleChatFrameClient.btnNouveauSalon.text_1")); //$NON-NLS-1$
		btnNouveauSalon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String strSalon = JOptionPane.showInputDialog(frmEdd, "Saisissez le nom du salon",
						"Création d'un salon", JOptionPane.PLAIN_MESSAGE);
				sender.setMsgToSend(
						IfClientServerProtocol.CREATE_CHANNEL + IfClientServerProtocol.SEPARATOR + strSalon);
			}
		});
		toolBar.add(btnNouveauSalon);

		cbSalonJoignable = new JComboBox();
		cbSalonJoignable.setMinimumSize(new Dimension(20, 22));
		cbSalonJoignable.setMaximumRowCount(100);
		cbSalonJoignable.setEditable(true);
		cbSalonJoignable.setSize(10, 10);

		toolBar.add(cbSalonJoignable);

		JButton btRejoindreSalon = new JButton(Messages.getString("SimpleChatFrameClient.btnNewButton.text"));
		btRejoindreSalon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sender.setMsgToSend(IfClientServerProtocol.USER_JOIN_CHANNEL + IfClientServerProtocol.SEPARATOR
						+ cbSalonJoignable.getSelectedItem());
			}
		});
		toolBar.add(btRejoindreSalon);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane();
		tabbedPane.addTab("Général", null, splitPane, null);

		JList<String> list = new JList<String>(this.hMapListModel.get(0));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int iFirstSelectedElement = ((JList) e.getSource()).getSelectedIndex();
				if (iFirstSelectedElement >= 0
						&& iFirstSelectedElement < SimpleChatFrameClient.this.hMapListModel.get(0).getSize()) {
					senderName = SimpleChatFrameClient.this.hMapListModel.get(0).getElementAt(iFirstSelectedElement);
					getLblSender().setText(senderName);
				} else {
					getLblSender().setText("?"); //$NON-NLS-1$
				}
			}
		});
		list.setMinimumSize(new Dimension(100, 0));
		splitPane.setLeftComponent(list);

		JPopupMenu popupMenu_1 = new JPopupMenu();
		addPopup(list, popupMenu_1);

		JMenuItem mntmSalonPriv = new JMenuItem(Messages.getString("SimpleChatFrameClient.chckbxmntmSalonPriv.text_1")); //$NON-NLS-1$
		mntmSalonPriv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getUserName().equals(list.getSelectedValue())) {
					//System.out.println(getUserName() + "/" + list.getSelectedValue());
					sender.setMsgToSend(
							IfClientServerProtocol.CREATE_CHANNEL + IfClientServerProtocol.SEPARATOR +
							IfClientServerProtocol.CHANNEL_PRIVATE + IfClientServerProtocol.SEPARATOR + getUserName() + "/" + list.getSelectedValue());
				}

			}
		});
		mntmSalonPriv.setEnabled(true);
		popupMenu_1.add(mntmSalonPriv);

		// JTextPane textArea = new JTextPane((StyledDocument)documentModel);
		JTextPane textArea = new JTextPane((StyledDocument) hMapDocumentModel.get(0));
		MyDocumentListener docListener = new MyDocumentListener();
		docListener.setTextPane(textArea);
		textArea.getDocument().addDocumentListener(docListener);
		textArea.setEnabled(false);
		JScrollPane scrollPaneText = new JScrollPane(textArea);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(textArea, popupMenu);

		JCheckBoxMenuItem chckbxmntmLock = new JCheckBoxMenuItem(Messages.getString("SimpleChatFrameClient.10")); //$NON-NLS-1$
		chckbxmntmLock.setEnabled(isScrollLocked);
		popupMenu.add(chckbxmntmLock);
		chckbxmntmLock.addActionListener(lockAction);

		scrollPaneText.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (isScrollLocked) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			}
		});

		splitPane.setRightComponent(scrollPaneText);
	}

	public JLabel getLblSender() {
		return lblSender;
	}

	private abstract class ResourceAction extends AbstractAction {
		public ResourceAction() {
		}
	}

	public void creerSalonJoignable(String userName, String salonName) {
		cbSalonJoignable.addItem(salonName);
	}

	public void supprimerSalonJoignable(String salonName) {
		cbSalonJoignable.removeItem(salonName);
	}
	
	public void ajouterUserSalon(String userLogin, int indexSalon) {
		System.out.println("ajouterusersalon" + userLogin + indexSalon);

		this.hMapListModel.get(indexSalon).addElement(userLogin);
	}

	public void creerSalon(String userName, String salonName, int indexSalon) {
		//Integer key = 1;
		ajoutSalon(indexSalon);
		Document documentModel = this.hMapDocumentModel.get(indexSalon);
		ListModel<String> listModel = this.hMapListModel.get(indexSalon);
		this.hMapListModel.get(indexSalon).addElement(userName);

		JSplitPane splitPane2 = new JSplitPane();
		tabbedPane.addTab(salonName, null, splitPane2, null);

		JList<String> list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int iFirstSelectedElement = ((JList) e.getSource()).getSelectedIndex();
				if (iFirstSelectedElement >= 0 && iFirstSelectedElement < listModel.getSize()) {
					senderName = listModel.getElementAt(iFirstSelectedElement);
					getLblSender().setText(senderName);
				} else {
					getLblSender().setText("?"); //$NON-NLS-1$
				}
			}
		});

		list.setMinimumSize(new Dimension(100, 0));
		splitPane2.setLeftComponent(list);

		JTextPane textArea = new JTextPane((StyledDocument) documentModel);
		MyDocumentListener docListener = new MyDocumentListener();
		docListener.setTextPane(textArea);
		textArea.getDocument().addDocumentListener(docListener);
		textArea.setEnabled(false);
		JScrollPane scrollPaneText2 = new JScrollPane(textArea);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(textArea, popupMenu);
		splitPane2.setRightComponent(scrollPaneText2);
	}

	private class SendAction extends ResourceAction {
		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}

		public SendAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.3")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.2")); //$NON-NLS-1$
			putValue(SMALL_ICON, getIcon());
		}

		public void actionPerformed(ActionEvent e) {
			sendMessage();
		}
	}

	private class LockAction extends ResourceAction {
		public LockAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.1")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.0")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			isScrollLocked = (!isScrollLocked);
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

}
