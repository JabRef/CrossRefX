package gui.dialog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ctex.Main;

public class ConditionalListener implements ActionListener{
	
	private JPanel ScrollPane;
	
	private JPanel panel;
	
	private JDialog dialog;
	private JComboBox Type;
	private JTextField ValueFild;
	private JTextField conditionFild;
	private String entryType;
	
	public ConditionalListener(JPanel ScrollPane, JPanel Panel, JDialog Dialog, String entryType, 
								    JTextField entryString, JComboBox Type, JTextField TypeName) {
		this.ScrollPane = ScrollPane;
		this.Type = Type;
		this.ValueFild = TypeName;
		this.entryType = entryType;
		this.conditionFild = entryString;
		this.panel = Panel;
		this.dialog = Dialog;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "+"){
			if (ValueFild.getText().isEmpty() == true || conditionFild.getText().isEmpty() == true){
				JOptionPane.showMessageDialog(null, Main.myLang.getString("cond.errormessage"), 
						Main.myLang.getString("cond.errortitle"), JOptionPane.OK_OPTION);
			} else {
				panel.add(getOneCondtion());
				ValueFild.setText("");
				dialog.pack();
			}
		} else if (e.getActionCommand() == "-"){
			ScrollPane.remove(panel);
			dialog.pack();
		}
	}
	
	private JPanel getOneCondtion(){
		final JPanel condition = new JPanel(new GridLayout(0, 5, 5, 5));
		java.net.URL imgURL = getClass().getClassLoader().getResource("images/delete.png");
		JButton del = new JButton(new ImageIcon(imgURL));
		JPanel BPanel = new JPanel();
		
		condition.add (getLabel(entryType));
		condition.add (getLabel(conditionFild.getText()));
		condition.add (getLabel(Type.getSelectedItem().toString()));
		condition.add (getLabel(ValueFild.getText()));
		del.setPreferredSize(new Dimension(20,20));
		BPanel.add(new JLabel("          "));
		BPanel.add(del);
		condition.add (BPanel);
		conditionFild.setEditable(false);
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.remove(condition);
				if (panel.getComponentCount() == 1){
					conditionFild.setEditable(true);
				}
				dialog.pack();
			}
		});
		condition.setPreferredSize(new Dimension(625, 25));
		return condition;
	}
	
	private JLabel getLabel(String Name){
		JLabel myLabel = new JLabel(Name);
		myLabel.setPreferredSize(new Dimension(140, 25));
		myLabel.setHorizontalAlignment(JLabel.CENTER);
		myLabel.setVerticalAlignment(JLabel.CENTER);
		return myLabel;
	}
}
