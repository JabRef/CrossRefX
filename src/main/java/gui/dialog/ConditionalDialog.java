package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import container.ContainerAttribute;
import container.ContainerType;
import ctex.Main;
import database.Db;
import gui.TextArea;


public class ConditionalDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	// Type
	private String entryType;
	// Condition ID
	int conditioncount = 0;
	// ScrollPane
	private JScrollPane Scroll;
	// Panel at scroll
	private JPanel ScrollPane;
	// Model fuer das GridBagLayout Anordnung von Condition "Bedingungen"
	GridBagConstraints constrain = new GridBagConstraints();
	// Types fuer das Combobox
	private Vector<String> Types = new Vector<String>();
	// Eingabe Felder fuer Typs
	private JTextField TypeSting = new JTextField();
	private JTextField ConditionString = new JTextField();
	private TextArea myTextArea;
	
	private Db theDb;
	
	public ConditionalDialog(String entryType, TextArea myText, Db db){
		this.theDb = db;
		this.entryType = entryType;
		this.myTextArea = myText;
		Vector<ContainerType> vector = theDb.getAllTypes();
		for (int i = 0; i < vector.size(); i++){
			Types.add(vector.get(i).getType());
		}
		setTitle("Condition " + entryType);
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(getTypePane(this), BorderLayout.PAGE_START);
		ScrollPane = new JPanel();
		ScrollPane.setLayout(new BoxLayout(ScrollPane, BoxLayout.Y_AXIS));
		Scroll = new JScrollPane();
		Scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Scroll.setViewportView(ScrollPane);
		
		getContentPane().add(Scroll, BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.PAGE_END);
		
		if (myTextArea.getAtt() != null){
			fillDialog();
		}
		
		pack();
		setVisible(true);
	}
	
	private JPanel getTypePane(final JDialog d){
		JPanel myTypePane = new JPanel();
		
		myTypePane.add(new JLabel("Type: " + entryType));
		
		java.net.URL imgURL = getClass().getClassLoader().getResource("images/add.png");
		JButton addB = new JButton(new ImageIcon(imgURL));
		addB.setPreferredSize(new Dimension(30,30)); 
		addB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (d.getHeight() > 500){
					d.setPreferredSize(new Dimension(649, 519));
				}			
				constrain.anchor = GridBagConstraints.NORTHWEST;
				constrain.fill = GridBagConstraints.NONE;
				constrain.gridx = 0;
				constrain.gridy = GridBagConstraints.RELATIVE;
				constrain.gridwidth = 1;
				constrain.gridheight = 1;
				constrain.weightx = 1; 
				constrain.weighty = 1;
				ScrollPane.add(getConditionPane());
				Scroll.revalidate();
				pack();
			}
		});
		myTypePane.add(addB);
		
		return myTypePane;
	}
	
	private JPanel getConditionPane(){
		conditioncount++;
		
		final JPanel conditionPane = new JPanel();
		JPanel BPanel = new JPanel();
		conditionPane.setLayout(new BoxLayout(conditionPane, BoxLayout.Y_AXIS));
		
		final JPanel eingabePane = new JPanel(new GridLayout(0, 6, 5, 5));
		
		eingabePane.add(new JLabel("Condition " + conditioncount), new  GridBagConstraints());
		
		ConditionString = new JTextField();
		eingabePane.add(ConditionString);
		eingabePane.add(new JLabel("     Type"));
		
		JComboBox myTypeLabel = new JComboBox(Types);
		myTypeLabel.setPreferredSize(new Dimension(100, 25));
		eingabePane.add(myTypeLabel);
		
		TypeSting = new JTextField();
		TypeSting.setPreferredSize(new Dimension(100, 25));
		eingabePane.add(TypeSting);
		
		java.net.URL imgURL = getClass().getClassLoader().getResource("images/add.png");
		JButton addB = new JButton(new ImageIcon(imgURL));
		addB.setActionCommand("+");
		addB.setName(" " + conditioncount);
		addB.setPreferredSize(new Dimension(20,20));
		BPanel.add(addB);
		ConditionalListener listenner = new ConditionalListener(ScrollPane, conditionPane, this, entryType, 
																		  ConditionString, myTypeLabel, TypeSting);
		addB.addActionListener(listenner);
		
		imgURL = getClass().getClassLoader().getResource("images/delete.png");
		JButton del = new JButton(new ImageIcon(imgURL));
		del.setActionCommand("-");
		del.addActionListener(listenner);
		del.setPreferredSize(new Dimension(20,20));
		BPanel.add(del);
		
		eingabePane.add(BPanel);
		conditionPane.add(eingabePane);
		conditionPane.setBorder(BorderFactory.createRaisedBevelBorder());
		//conditionPaneVek.add(conditionPane);
		
		return conditionPane;
		
	}
	
	private JPanel getButtonPanel(){
		JPanel panel = new JPanel();
		JButton okB = new JButton(Main.myLang.getString("button.ok"));
		okB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {	
				setConditionToTextArea();
				dispose();
			}
		});
		panel.add(okB);
		JButton removeB = new JButton(Main.myLang.getString("button.remove"));
		removeB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				myTextArea.removeCondition();
				dispose();
			}
		});
		panel.add(removeB);
		JButton closeB = new JButton(Main.myLang.getString("button.cancel"));
		closeB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(closeB);
		return panel;
	}
	
	private void setConditionToTextArea (){
		Vector<ContainerAttribute> v = new Vector<ContainerAttribute>();
		Vector<ContainerAttribute> v2 = null;
		String Bedinung = "", Type = "", Value = "";
		JScrollPane x = (JScrollPane)getContentPane().getComponent(1);
		JPanel d1 = (JPanel)x.getViewport().getView();
		for (int i = 0; i < d1.getComponentCount(); i++){			
			JPanel d2 = (JPanel) d1.getComponent(i);
			v2 = new Vector<ContainerAttribute>();
			for (int k = 1; k < d2.getComponentCount(); k++){
				JPanel Line = (JPanel)d2.getComponent(k);
				Bedinung = ((JLabel) Line.getComponent(1)).getText();
				Type = ((JLabel) Line.getComponent(2)).getText();
				Value = ((JLabel) Line.getComponent(3)).getText();
				
				v2.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(Type), Value, false, null));
			}
			v.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(entryType), Bedinung, false, v2));
		}
		myTextArea.setCondition(v);
	}
	
	private void fillDialog(){
		
		Vector<ContainerAttribute> v = myTextArea.getAtt();
		Iterator <ContainerAttribute> i = v.iterator();
		
		int count = 0;
		while (i.hasNext()){
			// Bedinung
			ContainerAttribute o = i.next();
			// Condition
			Vector<ContainerAttribute> v2 =o.getCondition();
			Iterator<ContainerAttribute> i2 = v2.iterator();
			// neue Panel mit Bedingungen
			ScrollPane.add(getConditionPane());
			JPanel d1 = (JPanel) ScrollPane.getComponent(count);
			JPanel d2 = (JPanel) d1.getComponent(0);
			
			while (i2.hasNext()){
				ContainerAttribute o2 = i2.next();
				
				// Bedinung
				JTextField Bedin = (JTextField) d2.getComponent(1);
				Bedin.setText(o.getValue());
				
				// Type
				JComboBox Type = (JComboBox) d2.getComponent(3);
				
				// TODO:
				// An der Stelle tut nicht da der ID -1 ist leeres String zurueck
				Type.setSelectedItem(theDb.convTypeIdToTypeName((o2.getObjectTypeId())));
				// Type Value
				JTextField TypeValue = (JTextField) d2.getComponent(4);
				TypeValue.setText(o2.getValue());
				
				JPanel d3 = (JPanel) d2.getComponent(5);
				JButton add = (JButton) d3.getComponent(0);
				// Klick machen und ins Panel einfuegen
				add.doClick();
			}
			count++;
		}
	}
	
	
}
