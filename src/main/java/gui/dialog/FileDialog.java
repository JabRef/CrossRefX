package gui.dialog;

import gui.FileTableModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import container.ContainerFile;

import ctex.Main;

public class FileDialog extends JDialog {
	private static final long serialVersionUID = -2465687149223378187L;
	
	private JTextField tLink;
	private JTextField tDescription;
	private JTextField tType;
	private FileDialog me;
	private FileTableModel modul;
	private ContainerFile cFile;
	private boolean isNew;
	private int row;
	
	public FileDialog (FileTableModel m, Component c){
		super();
		me = this;
		modul = m;
		this.isNew = true;
		setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.getContentPane().add(getCenterPanel());
		this.getContentPane().add(Box.createRigidArea(new Dimension(0, 5)));
		this.getContentPane().add(getBottomPanel());
		this.getContentPane().add(Box.createRigidArea(new Dimension(0, 5)));
		pack();
		setModal(true);
		setVisible(true);
	}
	
	public FileDialog (FileTableModel m, ContainerFile f, int row, Component c){
		super();
		me = this;
		modul = m;
		cFile = f;
		this.row = row;
		this.isNew = false;
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.getContentPane().add(getCenterPanel());
		this.getContentPane().add(Box.createRigidArea(new Dimension(0, 5)));
		this.getContentPane().add(getBottomPanel());
		this.getContentPane().add(Box.createRigidArea(new Dimension(0, 5)));
		pack();
		setModal(true);
		setVisible(true);
	}
	
	
	private JPanel getCenterPanel(){
		JPanel p = new JPanel();
		
		JLabel lLink = new JLabel ("Link:");
		JLabel lDiscription = new JLabel ("Description:");
		JLabel lType = new JLabel ("Type:");
		
		tLink = new JTextField();
		tLink.setPreferredSize(new Dimension(200, 22));
		tLink.setEditable(false);
		tDescription = new JTextField();
		tType = new JTextField ("Type");
		tType.setEditable(false);
		
		if (isNew == false){
			tLink.setText(cFile.getLink());
			tDescription.setText(cFile.getDescription());
			tType.setText(cFile.getType());
		}
		
		JButton browse = new JButton(Main.myLang.getString("button.browse"));
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFile = new JFileChooser();
				// I think all kinds of files are supported
				//openFile.setFileFilter(new CTex_FileFilter("Portable Document Format (*.pdf)", new String[] {"pdf"}));
				int returnVal = openFile.showOpenDialog(me);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File f = openFile.getSelectedFile();
					tLink.setText(f.getPath());
					tType.setText(f.getPath().substring(f.getPath().lastIndexOf('.') + 1));
				}
			}
		});
		if (isNew == false){
			browse.setEnabled(false);
		}
		
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		p.add(lLink, c);
		c.gridx++;
		p.add(tLink, c);
		c.gridx++;
		p.add(browse, c);
		c.gridx = 0;
		c.gridy++;
		p.add(lDiscription, c);
		c.gridx++;
		c.gridwidth = 2;
		p.add(tDescription, c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy++;
		p.add(lType, c);
		c.gridx++;
		c.gridwidth = 2;
		p.add(tType, c);
		return p;
	}
	
	private JPanel getBottomPanel (){
		JPanel p = new JPanel();
		JButton okButton = new JButton(Main.myLang.getString("button.ok"));
		JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isNew == true) {
					modul.addData(new ContainerFile(tDescription.getText(), tLink.getText(), tType.getText()));
				} else {
					modul.editData(new ContainerFile(cFile.getId(), tDescription.getText(), tLink.getText(), tType.getText()), row);
				}
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(okButton);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(cancelButton);
		return p;
	}

}
