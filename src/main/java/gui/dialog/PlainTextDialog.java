package gui.dialog;
import gui.FilenameFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import ctex.Main;


import database.Db;


/**
 * 
 * 
 * Driver: 	Apache PDFBox - Java PDF Library
 * License: Apache License v2.0. 
 * Driver	http://sourceforge.net/projects/pdfbox/files/PDFBox/
 * Version: PDFBox-0.7.3
 * Driver 	JAR Files PDFBox-0.7.3.jar, FontBox-0.1.0-dev.jar
 * 
 * 
 * TODO: autocomplete??? + add PDFLink to DB??? + markieren in TextPane: warscheinlich AbstractDocument
 */

public class PlainTextDialog extends javax.swing.JDialog {
	private static Logger logger = Logger.getLogger(PlainTextDialog.class);

	private static final long serialVersionUID = 1L;
	private javax.swing.JTextPane textArea = new javax.swing.JTextPane();
	private javax.swing.JButton autoText = new javax.swing.JButton(Main.myLang.getString("plain.auto"));
	private File file;
	private String entryType = "";
	private Db theDb = null;
	private int obj = -1;
	
	
	
	
	/* 
	 *  Panel mit AuswahlListe von Optionen
	 */
	private javax.swing.DefaultListModel typeListModel = new javax.swing.DefaultListModel();
	private javax.swing.JList typeList = new javax.swing.JList(typeListModel);
	
	
	/**
	 * 	@Constructor: CTex_Plain_Text_Dialog
	 * 
	 * 	input: String entryType
	 *  input: CTex_Db theDb
	 * 
	 */
	public PlainTextDialog(String entryType,  Db theDb){
		
		this.theDb = theDb;
		this.entryType = entryType;
		setTitle(Main.myLang.getString("plain.Text.title") + " " + entryType);
	
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);
		setLayout(new BorderLayout());
		setPreferredSize(new java.awt.Dimension(700, 500));
		
		// Toolbar
		getContentPane().add(getToolbar(), BorderLayout.PAGE_START);
		
		// Haupt Panel "Text Area und JList mit erlaublen Typs
		getContentPane().add(getCenterPanel());
		
		getContentPane().add(getButtomPanel(), BorderLayout.PAGE_END);
		
		pack();
		setVisible(true);
	
	}
	
	
	/**
	 * 	@Method: getToolbar
	 * 
	 * 	output: JToolBar
	 * 
	 *  initialisiert Toolbar
	 */
	private javax.swing.JToolBar getToolbar(){
		
		javax.swing.JToolBar toolbar = new javax.swing.JToolBar();
		javax.swing.JButton claerArea = new javax.swing.JButton(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/page.png")));
		claerArea.setToolTipText(Main.myLang.getString("plain.Text.cleartipp"));
		javax.swing.JButton openFile = new javax.swing.JButton(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/folder.png")));
		openFile.setToolTipText(Main.myLang.getString("menu.file.openfile"));
		
		claerArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		
		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pdfExtractor();
			}
		});
		
		toolbar.setFloatable(false);
		toolbar.add(claerArea);
		toolbar.add(openFile);
		
		return toolbar;
	}
	
	
	/**
	 * 	@Method: getCenterPanel
	 * 
	 * 	output: JPanel
	 * 
	 *  initialisiert die mitlere Panel aus TextArea und TypeList
	 */
	private javax.swing.JPanel getCenterPanel(){
		
		javax.swing.JPanel centerPanel = new javax.swing.JPanel();
		centerPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.insets = new Insets(2, 2, 2, 2);
        
        centerPanel.add(getTextArea(), cons);
        cons.gridx = 1;
        centerPanel.add(getTypeList(), cons);
        
        return centerPanel;
	}
	
	
	/**
	 * 	@Method: getTextArea
	 * 
	 * 	output: JPanel
	 * 
	 *  initialisiert TextArea
	 */
	private javax.swing.JPanel getTextArea(){
		javax.swing.JPanel textPanel = new javax.swing.JPanel();
		TitledBorder title = BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(java.awt.Color.black), Main.myLang.getString("plain.paste.text"));
        title.setTitleJustification(TitledBorder.CENTER);
        textPanel.setBorder(title);
        
        javax.swing.JScrollPane scroll = getScrolpane(textArea);
        scroll.setPreferredSize(new java.awt.Dimension(530, 370));
		
        textPanel.add(scroll);
        
        return textPanel;
	}
	
	
	/**
	 * 	@Method: getScrolpane
	 * 
	 * 	output: JScrollPane
	 * 
	 *  fuer TextArea und Type List
	 */
	private javax.swing.JScrollPane getScrolpane(java.awt.Component comp){
		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(comp);
		return scrollPane;
	}
	
	
	/**
	 * 	@Method: getButtomPanel
	 * 
	 * 	output: JPanel
	 * 
	 *  initialisiert die untere Bottom Panel
	 */
	private javax.swing.JPanel getButtomPanel(){
		
		javax.swing.JPanel buttomPanel = new javax.swing.JPanel();
		javax.swing.JButton accept = new javax.swing.JButton(Main.myLang.getString("plain.accept"));
		javax.swing.JButton canel = new javax.swing.JButton(Main.myLang.getString("button.cancel"));
		
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent con) {
				savetoDb();
			}
		});
		
		canel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent con) {
				dispose();
			}
		});
		
		buttomPanel.add(accept);
		buttomPanel.add(canel);
		return buttomPanel;
		
	}
	
	
	/**
	 * 	@Method: getTypeList
	 * 
	 * 	output: JPanel
	 * 
	 *  initialisiert die Liste mit allowed Typs
	 */
	private javax.swing.JPanel getTypeList(){
		
		javax.swing.JPanel typeListPanel = new javax.swing.JPanel();
		typeListPanel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.insets = new Insets(2, 2, 2, 2);
        
		inittypeList();
        
		TitledBorder title = BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(java.awt.Color.black), Main.myLang.getString("plain.allowed.field"));
        title.setTitleJustification(TitledBorder.CENTER);
        typeListPanel.setBorder(title);
        typeList.setCellRenderer(new myrenderer());
        typeList.addMouseListener(new TypeListMousListner(textArea, typeList, typeListModel));
        
        javax.swing.JScrollPane scroll = getScrolpane(typeList);
        scroll.setPreferredSize(new java.awt.Dimension(120, 370));
        typeListPanel.add(scroll);
        
        cons.gridy = 1;
        
        typeListPanel.add(autoText, cons);
        
        
        return typeListPanel;
        
		
	}
	
	

	/**
	 * 	@Procedure: inittypeList
	 * 
	 *  initialisiert den JList mit den Req. und Opt. Field aus DB
	 */
	private void inittypeList(){
		
		container.ContainerType type = theDb.getType(this.entryType);
		
		/* Die Requiedfields */
		for (int i = 0; i < type.getRequiredFields().size(); i++){
			typeListModel.addElement(new typeLabel(type.getRequiredFields().get(i).toString()));
		}
		
		/* Die Optionalfields */
		for (int i = 0; i < type.getOptionalFields().size(); i++){
			typeListModel.addElement(new typeLabel(type.getOptionalFields().get(i).toString()));
		}
		
	}
	
	
	
	/**
	 * 	@Procedure: pdfExtractor
	 * 
	 *  Ruft den FileChooser, startet den pdfParser oder TODO: txtParser
	 *  und setzt den Inhalt von TextArea
	 */
	private void pdfExtractor(){
		
		String pdfText = null;
		
		javax.swing.JFileChooser openFile = new javax.swing.JFileChooser();
		openFile.setFileFilter(new FilenameFilter("Pdf (*.pdf) and Text (*.txt) files", new String[] {"pdf", "txt"}));
		int returnVal = openFile.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            this.file = openFile.getSelectedFile();
            try {
            	// Setting the path of the opened file
            	if (file.getPath().endsWith(".pdf")) {
            		pdfText = pdfParser(file);
            		textArea.setText(pdfText);
            	} else if (file.getPath().endsWith(".txt")) {
            		/*
            		 * TODO: Parse of .txt
            		 */
            	}
           
            } catch (Exception e) {
            	System.out.println("Error:File");
            }
		}
	}
	
	
	
	/**
	 * 	@Method: pdfParser
	 * 
	 * 	input : File
	 *  output: String
	 *  
	 *  Diese Methode lie�t den Text aus der Pdf Datei aus und gibt den Text als String zur�ck
	 */
	private String pdfParser(File pdfFile){
		NDC.push("pdfParser");
		
        PDDocument document = null;
        try {
			document = PDDocument.load( pdfFile );
		} catch (IOException e) {
			logger.error("Could not load document", e);
			NDC.pop();
			return null;
		}

		if (document.isEncrypted()) {
			NDC.pop();
			return "Encrypted documents are not supported";
		}
		
		PDFTextStripper stripper;
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			logger.error("Could not create stripper", e);
			NDC.pop();
			return null;
		}
		
		stripper.setStartPage(1);
		stripper.setEndPage(2);
		
		String text;
		try {
			text = stripper.getText(document); 
		} catch (Exception e) {
			logger.error("Could not parse PDF", e);
			NDC.pop();
			return null;
		}
		NDC.pop();
        return text;
	}
	
	/**
	 *  TODO: Typs stehen in typeListModel!!!
	 */
	private void savetoDb(){
		
		
		String key = "CrossRefPdf_" + this.file.getName();
		int entryTypeId = theDb.addType(this.entryType, false, true, true);
		int entryId = theDb.addObject(entryTypeId, key);
		
		
		for (int i = 0; i < typeListModel.size(); i++){
			if (((gui.dialog.typeLabel) typeListModel.getElementAt(i)).getvalue() != null){
				int typeId = theDb.addType(((gui.dialog.typeLabel) typeListModel.getElementAt(i)).getName(), false, false, true);
				theDb.addTypeLink(entryTypeId, typeId, false);
				int attributeId = theDb.addAttribute(typeId, ((gui.dialog.typeLabel) typeListModel.getElementAt(i)).getvalue(), false);
				this.obj = theDb.addObjectLink(entryId, attributeId);
				this.obj = theDb.getObject(key).getId();
			}
		}
		
		if (this.file.getPath().endsWith(".pdf")){
			int typeId = theDb.addType("pdfField", false, false, true);
			theDb.addTypeLink(entryTypeId, typeId, false);
			int attributeId = theDb.addAttribute(typeId, this.file.getPath(), false);
			this.obj = theDb.addObjectLink(entryId, attributeId);
			this.obj = theDb.getObject(key).getId();
		
		}
		
		
		
		this.dispose();
	}
	
	public int getObj(){
		return this.obj;
	}

}

	/**
	 * 	Objekt der in JList angezeigt wird
	 */
class typeLabel extends javax.swing.JLabel{
	
	
	private static final long serialVersionUID = 1L;
	private boolean enabel = false;
	private String Name = null;
	private String value = null;
	
	typeLabel(String text){
		this.Name = text;
		this.setText(text);
		
	}
	
	typeLabel(javax.swing.ImageIcon icon){
		this.setIcon(icon);
		
	}
	
	public void setEnabel(){
		this.enabel = true;
	}
	
	public boolean isEnable(){
		return enabel;
	}
	
	public String getName(){
		return Name;
	}
	
	public void setvalue(String value){
		this.value = value;
	}
	
	public String getvalue(){
		return this.value;
	}

}


	/**
	 * 	
	 */
class myrenderer extends gui.dialog.typeLabel implements ListCellRenderer {
		 
	private static final long serialVersionUID = 1L;
	
	public  myrenderer () {
		super((new javax.swing.ImageIcon ("images/icon.gif")));
	}
		 
	public java.awt.Component getListCellRendererComponent (javax.swing.JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
	     if(value instanceof String) {
	    	 this.setText((String) value);
	     } else if (value instanceof gui.dialog.typeLabel) {
		     this.setText(((gui.dialog.typeLabel) value).getText());
		     this.setFont(new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 16));
		     this.setVerticalTextPosition(1);
		     this.setHorizontalAlignment(javax.swing.JLabel.LEFT);
		 }

	     
	     if(isSelected){
	    	 if (!((gui.dialog.typeLabel) value).isEnable()){
	    		 this.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/icon.gif")));
	    	 } else {
	    		 this.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/mark.png")));
	    	 }
	    	 this.setBackground(java.awt.Color.LIGHT_GRAY);
	    	 this.setOpaque(true);
		 } else {
			 if (!((gui.dialog.typeLabel) value).isEnable()) {
				 this.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/icon.gif")));
		     } else {
		    	 this.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/mark.png")));
			 }
			 this.setBackground(java.awt.Color.WHITE);
	    	 this.setOpaque(true);
		 }
	     
		 return (this) ;
		        
	}
}

	/**
	 *  Class TypeListMousListner
	 *  
	 *  ueberwacht die actionen von JList
	 *
	 */
class TypeListMousListner implements MouseListener{

	private javax.swing.DefaultListModel listModel = new javax.swing.DefaultListModel();
	private javax.swing.JTextPane textArea = new javax.swing.JTextPane();
	private javax.swing.JList list = new javax.swing.JList();
	
	private int markierenStart = -1;
	private int markierenEnd = -1;
	
	TypeListMousListner(javax.swing.JTextPane textArea, javax.swing.JList list, javax.swing.DefaultListModel listModel){
		this.textArea = textArea;
		this.listModel = listModel;
		this.list = list;
	}
	
	
	
	public void mouseClicked(MouseEvent e) {
		
		if (e.getClickCount() == 2) {
			
			StyledDocument doc  = this.textArea.getStyledDocument();
            Style farbe        = doc.addStyle("farbe", null);            
            
            /*
             * Set Atribut value of Type
             */
            if (textArea.getSelectedText() != null) {
				((gui.dialog.typeLabel) listModel.getElementAt(list.getSelectedIndex())).setEnabel();
				((gui.dialog.typeLabel) listModel.getElementAt(list.getSelectedIndex())).setvalue(textArea.getSelectedText());
				list.repaint();
			}
            
            /*
             * Set Selected Style of TextPane Document
             */
            StyleConstants.setBold(farbe, true);
            
            if(this.markierenStart == -1){
				if (this.textArea.getSelectedText() != null) {
					StyleConstants.setForeground(farbe, Color.RED);
					doc.setCharacterAttributes(this.textArea.getSelectionStart(), 
											   this.textArea.getSelectedText().length(), farbe, true);
					
					this.markierenStart = this.textArea.getSelectionStart();
					this.markierenEnd = this.textArea.getSelectionEnd();
				}
			} else {
				if (this.textArea.getSelectedText() != null) {
					StyleConstants.setForeground(farbe, Color.RED);
					doc.setCharacterAttributes(this.textArea.getSelectionStart(), 
											   this.textArea.getSelectedText().length(), farbe, true);
					StyleConstants.setForeground(farbe, Color.BLUE);
					doc.setCharacterAttributes(this.markierenStart, 
							   (this.markierenEnd-this.markierenStart), farbe, true);
					
					this.markierenStart = this.textArea.getSelectionStart();
					this.markierenEnd = this.textArea.getSelectionEnd();
					
				}
			}
			
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
}


/**
 * 
 * @author allendds
 *		   informationen fuer markierung speichern	
 */
class MarkText {
	
	int start = -1;
	int end = -1;
	
	MarkText(){}
	
}