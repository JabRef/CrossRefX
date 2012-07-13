package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import container.ContainerAttribute;
import container.ContainerObject;
import container.ContainerType;
import database.Db;

public class PreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JScrollPane previewScroll;
	private JTextPane previewPane;
	
	private TabPanel inTab;
	private ContainerObject previewObject;
	
	private GridBagConstraints cons = new GridBagConstraints();
	
	PreviewPanel(final TabPanel inTab, ContainerObject previewObject) {
		this.setLayout(new GridBagLayout());
		this.inTab = inTab;
		this.previewObject = previewObject;
		
		// Close dialog when pressing ESC
		inTab.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inTab.removeBottomComponentofSplitPane();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		previewPane = new JTextPane();
		previewPane.setEditable(false);
		previewPane.setContentType("text/html");
		previewPane.setText(setPreviewText());
		previewScroll = new JScrollPane(previewPane);
		cons.gridx = 0;
		cons.gridy = 0;
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = 1.0;
		cons.weighty = 1.0;
		cons.fill = GridBagConstraints.BOTH;
		this.add(previewScroll, cons);
	}
	
	public String setPreviewText() {
		String text = "";
		Db db = ((Model)inTab.getTheModel()).getEntity().getDb();
		String objectType = db.convTypeIdToTypeName(previewObject.getTypeId());
		ContainerType type = db.getType(objectType);
		
		text = text + "<span style=\"font-size: 18pt\"><i>" + objectType + "</i></span><br>";
		
		if (objectType.compareToIgnoreCase("article") == 0) {
			text += getAttribute(previewObject, "author") + "<br>";
			text += getAttribute(previewObject, "title") + "<br>";
			text += "<i>" + getAttribute(previewObject, "journal") + "</i> ";
			text += getAttribute(previewObject, "publisher") + " ";
			text += "<b>" + getAttribute(previewObject, "year") + "</b> ";
			text += getAttribute(previewObject, "pages") + " ";
			text += getAttribute(previewObject, "doi");
		} else if (objectType.compareToIgnoreCase("inproceedings") == 0) {
			text += getAttribute(previewObject, "author") + "<br>";
			text += getAttribute(previewObject, "title") + "<br>";
			text += "<i>" + getAttribute(previewObject, "conference") + "</i> ";
			text += getAttribute(previewObject, "series") + " ";
			text += "<i>" + getAttribute(previewObject, "volume") + "</i> ";
			text += "<i>" + getAttribute(previewObject, "publisher") + "</i> ";
			text += "<b>" + getAttribute(previewObject, "year") + "</b> ";
			text += getAttribute(previewObject, "doi");
		} else {
			for (int i = 0; i < type.getRequiredFields().size(); i++) {
				for (int j = 0; j < previewObject.getAttributes().size(); j++) {
					String attributeType = db.convTypeIdToTypeName(previewObject.getAttributes().get(j).getObjectTypeId());
					if (type.getRequiredFields().get(i).compareTo(attributeType) == 0) {
						text += previewObject.getAttributes().get(i).getValue() + "<br>";
					}
				}
			}
			
			for (int i = 0; i < type.getOptionalFields().size(); i++) {
				for (int j = 0; j < previewObject.getAttributes().size(); j++) {
					String attributeType = db.convTypeIdToTypeName(previewObject.getAttributes().get(j).getObjectTypeId());
					if (type.getOptionalFields().get(i).compareTo(attributeType) == 0) {
						text += previewObject.getAttributes().get(j).getValue() + "<br>";
					}
				}
			}
		}
		return text;		
	}
	
	/**
	 * Checks all attributes for a given type and returns the fitting attribute
	 * 
	 * @param attributeType
	 * @return the attribute with the fitting type
	 */
	public String getAttribute(ContainerObject object, String attributeType) {
		Db db = ((Model)inTab.getTheModel()).getEntity().getDb();
		Vector<ContainerAttribute> attributes = object.getAttributes();
		String result = "";
		for (int i = 0; i < object.getAttributes().size(); i++) {
			if (db.convTypeIdToTypeName((attributes.get(i).getObjectTypeId())).compareToIgnoreCase(attributeType) == 0) {
				result = attributes.get(i).getValue();
			}
		}
		return result;
	}

}
