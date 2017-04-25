package com.cfranc.irc.ui;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

public class MyDocumentListener implements DocumentListener {

	public MyDocumentListener() {
		super();
		try {// attempt to get icon for emoticons
//			smiley = new ImageIcon(ImageIO.read(new URL("http://hicksdesign.co.uk/folio/skype/Skype-panel-3.png"))
//					.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
			smiley = new ImageIcon(ImageIO.read(new URL("file:ressources/smile.png"))
					.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
			sad = new ImageIcon(ImageIO.read(new URL("file:ressources/sad.png"))
					.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
			angry = new ImageIcon(ImageIO.read(new URL("file:ressources/angry.png"))
					.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
			wink = new ImageIcon(ImageIO.read(new URL("file:ressources/wink.png"))
					.getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JTextPane textPane;

	static ImageIcon smiley, sad, angry, wink;
	static final String SMILEY_EMOTICON = ":)", SAD_EMOTICON = ":(", ANGRY_EMOTICON = "X(", WINK_EMOTICON=";)";
	String[] emoticons = { SMILEY_EMOTICON, SAD_EMOTICON, ANGRY_EMOTICON, WINK_EMOTICON };

	@Override
	public void insertUpdate(DocumentEvent e) {
		// We should surround our code with SwingUtilities.invokeLater() because
		// we cannot change document during mutation intercepted in the
		// listener.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {

					StyledDocument doc = (StyledDocument) e.getDocument();
					int start = Utilities.getRowStart(textPane, Math.max(0, e.getOffset() - 1));
					int end = Utilities.getWordStart(textPane, e.getOffset() + e.getLength());

					String text = doc.getText(start, end - start);

					for (String emoticon : emoticons) {// for each emoticon
						int i = text.indexOf(emoticon);
						while (i >= 0) {
							final SimpleAttributeSet attrs = new SimpleAttributeSet(
									doc.getCharacterElement(start + i).getAttributes());
							if (StyleConstants.getIcon(attrs) == null) {

								switch (emoticon) {// check which emoticon
													// picture to
													// apply
								case SMILEY_EMOTICON:
									StyleConstants.setIcon(attrs, smiley);
									break;
								case SAD_EMOTICON:
									StyleConstants.setIcon(attrs, sad);
									break;
								case ANGRY_EMOTICON:
									StyleConstants.setIcon(attrs, angry);
									break;
								case WINK_EMOTICON:
									StyleConstants.setIcon(attrs, wink);
									break;
								}

								doc.remove(start + i, emoticon.length());
								doc.insertString(start + i, emoticon, attrs);
							}
							i = text.indexOf(emoticon, i + emoticon.length());
						}
					}
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		System.out.println("changed");
	}

	public void setTextPane(JTextPane textPane) {
		this.textPane = textPane;
	}
}
