package gui.dialog;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import ctex.Main;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/** 
 * Help Dialog for displaying the help files in HTML format
 * 
 * @author stefan
 *
 */

public class HelpDialog extends JDialog implements HyperlinkListener {
	private static final long serialVersionUID = 1L;
	
	JEditorPane helpContent = new JEditorPane();
	
	/**
	 * Constructor setting up the swing components
	 */
	public HelpDialog() {
		setTitle(Main.myLang.getString("help.title"));
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		helpContent.setEditable(false);
		helpContent.setPreferredSize(new Dimension(600, 500));
		helpContent.addHyperlinkListener(this);
		try {
			/* Setting the index page of the help document. File converted to URI, then to URL
			 * because toURL() from File-Object is deprecated
			 */
			helpContent.setPage(new File("docs/usermanual/usermanual_en.html").toURI().toURL());
		} catch (IOException e) {
			System.out.println("Error opening webpage!");
			e.printStackTrace();
		}
		
		// JScrollPane to make scrolling HTML pages available
		JScrollPane helpScrollPane = new JScrollPane(helpContent);
		getContentPane().add(helpScrollPane, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Checks if a hyperlink gets clicked. Only allows local html files to be
	 * linked, online files will be opened with the default browser.
	 * 
	 * @param HyperlinkEvent e: any hyperlink event
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		Desktop desk = null;
		// Without this check every touch of a hyperlink is seen as activation
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				if (!e.getURL().toString().startsWith("http")) {
					helpContent.setPage(e.getURL());
				} else {
					if (Desktop.isDesktopSupported())
						desk = Desktop.getDesktop();
						desk.browse(e.getURL().toURI());
					
				}				
			} catch (IOException e1) {
				System.err.println("Error opening webpage!");
				e1.printStackTrace();
			} catch (URISyntaxException e2) {
				System.err.println("Error opening webpage with native browser!");
				e2.printStackTrace();
			}
		}
	}
}
