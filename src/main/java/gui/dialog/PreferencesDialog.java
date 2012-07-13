package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import gui.ShortcutB;
import gui.TabPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ctex.Main;

public class PreferencesDialog extends javax.swing.JDialog implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private PreferencesDialog myDialog = null;
	
	private ShortcutB shortcuts = null;
	private JComboBox langbox = null;
	
	
	/* 
	 *  Panel mit AuswahlListe von Optionen
	 */
	private Vector<String> List = new Vector<String> ();
	private JList myList = new JList(List);
	
	/* Componente fuer jeden List Eintrag */
	private JComponent generalComponent = null;
	private JComponent shortcutComponent = null;
	private JComponent viewComponent = null;
	
	private JTextField workspaceLink = new JTextField();
	
	
	// For adjusting the view on the table
	private int columntype = Integer.parseInt(Main.getIniData("columntype"));
	private int filecolumn = Integer.parseInt(Main.getIniData("filecolumn"));
	
	private TabPanel tableTabPanel;
	
	
	private Vector<String> culumtypes = new Vector<String>();
	private JComboBox typeComboBox = new JComboBox();
	private DefaultListModel typeListModel = new DefaultListModel();
	
	private JList myTableTypeList = new JList(typeListModel);
	private JButton removeType = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/delete.png")));
	
	
	public PreferencesDialog(final ShortcutB shortcuts, TabPanel tableTabPanel){
		
		this.myDialog = this;
		this.shortcuts = shortcuts;
		this.tableTabPanel = tableTabPanel;
		setTitle(Main.myLang.getString("preferences.title"));
		
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //Add content to the window.
        setLayout (new BorderLayout());
       
        getOptList();
        
        generalComponent = getGeneralPanel();
        shortcutComponent = getHotKeyPanel();
        viewComponent = getViewPanel();
        
        /* add all Components */
        add(myList, BorderLayout.WEST);
        add(getButtonPanel(), BorderLayout.PAGE_END);
        add(generalComponent, BorderLayout.CENTER);
        
        setSize(750, 600);
        this.setResizable(false);
        setModal(true);
        setVisible(true);
		
	}
	
	/*
	 * TODO:Comment
	 */
	private void getOptList(){
		Border blackline = BorderFactory.createLineBorder(Color.black);
		List.add("  " + Main.myLang.getString("preferences.gerneral.preferences") + "  ");
        List.add("  " + Main.myLang.getString("preferences.tab.hotkey") + "  ");
        List.add("  " + Main.myLang.getString("preferences.tab.view") + "  ");
        myList.addListSelectionListener(this);
        myList.setBorder(blackline);
    }
    
	
	/*
	 * TODO:Comment
	 */
	private void getTableTypeList(){
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		String type = Main.getIniData("defaultcolumns");
		while (type.length() != 0){
			typeListModel.addElement(type.substring(0, type.indexOf(",")));
    		type = type.substring(type.indexOf(",") + 1);
    	}
		
		myTableTypeList.addMouseListener(new MouseListener() {

			
			public void mouseClicked(MouseEvent e) {
				removeType.setEnabled(true);
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		//myTableTypeList.addListSelectionListener(this);
		myTableTypeList.setBorder(blackline);
    }
    
    
	/*
	 * TODO:Comment
	 */
	private JComponent getHotKeyPanel (){
    	
    	TitledBorder title;
        title = BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(Color.black), Main.myLang.getString("preferences.tab.hotkey"));
        title.setTitleJustification(TitledBorder.CENTER);
        
        JPanel myPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    	
    	for (int i = 0; i < shortcuts.keys.size(); i++){
			myPanel.add(new JLabel(shortcuts.keys.get(i).getText()), null);
			JPanel ComboPanel = new JPanel(new GridLayout(0, 2, 5, 5));
	    	ComboPanel.add(shortcuts.keys.get(i).myStrgBox, null);
			ComboPanel.add(shortcuts.keys.get(i).myBuchBox, null);
			// anhangen an jeder comboBox das message ueber das doppelte belegung von Optionen
			shortcuts.keys.get(i).myStrgBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (shortcuts.exeption() == true) {
						JOptionPane.showMessageDialog(null,
								Main.myLang.getString("dialog.shortcut.message"),
							    Main.myLang.getString("dialog.shortcut.error"),
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			shortcuts.keys.get(i).myBuchBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (shortcuts.exeption() == true) {
						JOptionPane.showMessageDialog(null,
								Main.myLang.getString("dialog.shortcut.message"),
							    Main.myLang.getString("dialog.shortcut.error"),
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			myPanel.add(ComboPanel, null);
		}
		JScrollPane returnPane = new JScrollPane(myPanel);
    	returnPane.setBorder(title);
        
		return returnPane;
    }
    
	
	/*
	 * TODO:Comment
	 */
	private JComponent getGeneralPanel(){
    	
    	TitledBorder title;
        title = BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(Color.black), Main.myLang.getString("preferences.gerneral.preferences"));
        title.setTitleJustification(TitledBorder.CENTER);
        JPanel dummy = new JPanel();
        
    	Vector<String> theLang = new Vector<String> ();
    	// Ordnet die Componenete von oben nach unten
    	JPanel myPanel = new JPanel(new GridLayout(20, 1, 5, 5));
    	workspaceLink.setEditable(false);
    	workspaceLink.setText(Main.getIniData("Workspace"));
    	
    	JLabel langLable = new JLabel(Main.myLang.getString("preferences.gerneral.lang"), JLabel.CENTER);
		JLabel wokLable = new JLabel(Main.myLang.getString("preferences.gerneral.workspace"), JLabel.CENTER);
		langLable.setPreferredSize(new Dimension(150, 0));
		wokLable.setPreferredSize(new Dimension(150, 0));
		JButton browse = new JButton(Main.myLang.getString("button.browse"));
		browse.addActionListener(new Browselistener(this.myDialog, workspaceLink));

		
    	langbox = new JComboBox(theLang);
    	String soppLang = Main.getIniData("SupportedLanguage");
    	String akLang = Main.getIniData("Language");
    	
    	while (soppLang.length() != 0){
    		theLang.add(soppLang.substring(0, soppLang.indexOf(",")));
    		soppLang = soppLang.substring(soppLang.indexOf(",") + 1);
    	}
    	langbox.setSelectedItem(akLang);
    	
    	//Lay out the buttons from left to right.
    	dummy.setLayout(new BoxLayout(dummy, BoxLayout.LINE_AXIS));
    	dummy.add(langLable); 
    	dummy.add(langbox);
    	dummy.add(Box.createRigidArea(new Dimension(400, 0))); 
    	myPanel.add(dummy);
    	
    	dummy = new JPanel();
    	dummy.setLayout(new BoxLayout(dummy, BoxLayout.LINE_AXIS));
    	dummy.add(wokLable);
    	dummy.add(workspaceLink);
    	dummy.add(Box.createRigidArea(new Dimension(20, 0)));
    	dummy.add(browse);
    	dummy.add(Box.createRigidArea(new Dimension(100, 0)));
    	
    	myPanel.add(dummy);
    	
    	for (int i = 0; i < 18; i++){
    		myPanel.add(new JPanel());
        }
    	myPanel.setBorder(title);
    	
    	return myPanel;
    }
    
    // Panel for changing what is shown in the table
    private JComponent getViewPanel() {
        
    	JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new GridBagLayout());
    	TitledBorder title = BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(Color.black), Main.myLang.getString("preferences.tab.view"));
        title.setTitleJustification(TitledBorder.CENTER);
        viewPanel.setBorder(title);
        loadComboBoxFields();
        
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.anchor = GridBagConstraints.LINE_START;
        cons.insets = new Insets(2, 2, 2, 2);
        
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton showDefaultCols = new JRadioButton(Main.myLang.getString("preferences.view.showdefaultcolumns"));
        JRadioButton showAllCols = new JRadioButton(Main.myLang.getString("preferences.view.showallcolumns"));
        JRadioButton showUsedCols = new JRadioButton(Main.myLang.getString("preferences.view.showonlyusedcolumns"));
        final JCheckBox showFileCol = new JCheckBox(Main.myLang.getString("preferences.view.showfilecolumn"));
        
        JButton addType = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/add.png")));
        addType.setPreferredSize(new Dimension(23, 23));
        removeType.setPreferredSize(new Dimension(23, 23));
        removeType.setEnabled(false);
        
        buttonGroup.add(showDefaultCols);
        buttonGroup.add(showAllCols);
        buttonGroup.add(showUsedCols);
        
        viewPanel.add(showDefaultCols, cons);
   
        final JLabel columnInfo = new JLabel(Main.myLang.getString("preferences.view.columninfolabel"));
        cons.gridy = 1;
        viewPanel.add(columnInfo, cons);
        
        cons.gridy = 2;
        viewPanel.add(showFileCol, cons);
        
        getTableTypeList();
        JScrollPane columnInputScroll = new JScrollPane(myTableTypeList);
        columnInputScroll.setPreferredSize(new Dimension(300, 350));
        cons.gridy = 3;
        viewPanel.add(columnInputScroll, cons);
        
        // Type Box
        cons.gridx = 1;
        cons.anchor = GridBagConstraints.PAGE_START;
        viewPanel.add(typeComboBox, cons);
        
        
        // Add Button
        cons.gridx = 2;
        viewPanel.add(addType, cons);
        
        // remove Button
        cons.gridx = 3;
        viewPanel.add(removeType, cons);
        
        cons.anchor = GridBagConstraints.LINE_START;
        cons.gridx = 0;
        cons.gridy = 4;
        viewPanel.add(showAllCols, cons);
        cons.gridy = 5;
        viewPanel.add(showUsedCols, cons);        

        columnInfo.setEnabled(false);
        myTableTypeList.setEnabled(false);
        switch (columntype) {
        	case 1: showDefaultCols.setSelected(true);
        		columnInfo.setEnabled(true);
        		showFileCol.setEnabled(true);
        		myTableTypeList.setEnabled(true);
        		break;
        	case 2: showAllCols.setSelected(true);
        		break;
        	case 3: showUsedCols.setSelected(true);
        		break;
        }
        
        if (filecolumn == 1){
        	showFileCol.setSelected(true);
        } else {
        	showFileCol.setSelected(false);
        }
        
        showFileCol.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (filecolumn == 0){
        			filecolumn = 1;
        		} else  {
        			filecolumn = 0;
        		}
        	}
        });
        
        addType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(!listIncludeType(typeComboBox.getSelectedItem().toString())){
        			typeListModel.addElement(typeComboBox.getSelectedItem().toString());
        		}
        	}
        });
        
        removeType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		typeListModel.remove(myTableTypeList.getSelectedIndex());
        		removeType.setEnabled(false);
        	}
        });
        
        showDefaultCols.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		columntype = 1;
        		columnInfo.setEnabled(true);
        		showFileCol.setEnabled(true);
        		myTableTypeList.setEnabled(true);
        	}
        });
        
        showAllCols.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		columntype = 2;
        		columnInfo.setEnabled(false);
        		showFileCol.setEnabled(false);
        		myTableTypeList.setEnabled(false);
        	}
        });
        
        showUsedCols.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		columntype = 3;
        		columnInfo.setEnabled(false);
        		showFileCol.setEnabled(false);
        		myTableTypeList.setEnabled(false);
        	}
        });
        
    	return viewPanel;
    }
    
    
    private JComponent getButtonPanel(){
    	
    	JPanel myPanel = new JPanel(new FlowLayout());
    	JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
    	JButton okButton = new JButton(Main.myLang.getString("button.ok"));
    	myPanel.add(okButton);
    	myPanel.add(cancelButton);
    	okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					//  Die aenderungen werden nur dann uebernommen, wenn die
					//  Optionen nicht doppelt vorkommen
					if (Main.getIniData("Language").equals((String)langbox.getSelectedItem()) == false){
						Main.setIniData("Language", (String)langbox.getSelectedItem());
						Object[] options = {
								Main.myLang.getString("button.yes"),
								Main.myLang.getString("button.no")
								};
						int n = JOptionPane.showOptionDialog(null,
								Main.myLang.getString("dialog.lang.message"),
							    Main.myLang.getString("dialog.lang.error"),
							    JOptionPane.YES_NO_OPTION,
							    JOptionPane.QUESTION_MESSAGE,
							    null,
							    options,
							    options[1]);
						if (n == 0){
							//TODO: RESTART
							System.exit(0);
						}
						
					}
					if (shortcuts.exeption() == true) {
						JOptionPane.showMessageDialog(null,
								Main.myLang.getString("dialog.shortcut.message"),
							    Main.myLang.getString("dialog.shortcut.error"),
							    JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							shortcuts.storeKeys();
							dispose();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					Main.setIniData("Workspace", (String)workspaceLink.getText());
					
					// Saves the info which columns should be shown
					Main.setIniData("columntype", Integer.toString(columntype));
					Main.setIniData("filecolumn", Integer.toString(filecolumn));
					
					String columntypes = "";
					for(int i = 0; i < typeListModel.size(); i++){
						columntypes = columntypes + typeListModel.get(i) + ",";
					}
					Main.setIniData("defaultcolumns", columntypes);
					// Refreshes all existing models
					tableTabPanel.refreshAllModels();
					//
					//More Options to Save on OK place here
					//
					
			}
		});
    	cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return myPanel;
    }

    
    public void valueChanged(ListSelectionEvent e) {
		
		if (e.getValueIsAdjusting()) {
			String dummy = List.get(myList.getSelectedIndex()).substring(2, List.get(myList.getSelectedIndex()).length() - 2);
			
			if (dummy.equals(Main.myLang.getString("preferences.gerneral.preferences"))){
				myDialog.getContentPane().remove(2);
				myDialog.getContentPane().add(generalComponent, BorderLayout.CENTER);
			    
			} else if (dummy.equals(Main.myLang.getString("preferences.tab.hotkey"))){
				myDialog.getContentPane().remove(2);
				myDialog.getContentPane().add(shortcutComponent, BorderLayout.CENTER);
				
			} else if (dummy.equals(Main.myLang.getString("preferences.tab.view"))){
				myDialog.getContentPane().remove(2);
				myDialog.getContentPane().add(viewComponent);
			}
			myDialog.pack();
			myDialog.setSize(750, 600);
			
		}
	}
    
    /*
     * TODO: Comment
     */
    private void loadComboBoxFields() {
		
    	try {
			FileInputStream fileStream = new FileInputStream("types/fields.dat");
			DataInputStream in = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String field;
			
			while ((field = reader.readLine()) != null) {
				culumtypes.add(field);
				typeComboBox.addItem(field);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
    /*
     * TODO: Comment
     */
    private boolean listIncludeType(String type){
    	
    	int count = 0;
    	while (count != typeListModel.size() - 1 && !typeListModel.get(count).toString().equals(type)){
    		count++;
    	}
    	
    	if(typeListModel.get(count).toString().equals(type)){
    		return true;
    	} else {
    		return false;
    	}
    
    }
    
}

/*
 * TODO: Comment
 */
class Browselistener implements ActionListener{
	
	PreferencesDialog dialog;
	JTextField area;
	
	Browselistener(PreferencesDialog dialog, JTextField area){
		this.dialog = dialog;
		this.area = area;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if(chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
			area.setText(chooser.getSelectedFile().getPath());
		}
	}
	
}
