package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;


public class Statusbar extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Array der Felder, um spaeter auf diese zugreifen zu koennen
    private StatusbarField[] fields;
    
    public Statusbar(int numFields) {
       fields = new StatusbarField[numFields];

       // Der Einfachheit halber verwende ich hier
       // ein GridLayout. In der Praxis wuerde man
       // eventuell einen anderen LayoutManager
       // verwenden.
       setLayout(new GridLayout(1,numFields));

       // Hinzufuegen der Felder
       for(int i=0; i<numFields; i++) {
          fields[i] = new StatusbarField();
          add(fields[i]);
       }
    }

    // Methode, um den Text eines Feldes zu aendern
    public void setText(int num, String text) {
       fields[num].setText(text);
       fields[num].paintImmediately(fields[num].getVisibleRect());  
    }

    // Methode, um den Text eines Feldes auszulesen
    public String getText(int num) {
       return fields[num].getText();
    }

    // Das eigentlich Feld: ein einfaches JLabel
    class StatusbarField extends JLabel {
    	private static final long serialVersionUID = -8763845324779450119L;
    	
    	public StatusbarField() {
	          // Der Rand, der das Ganze wie eine typische
	          // Statusleiste erscheinen laesst
	          setBorder(new BevelBorder(BevelBorder.LOWERED));
	
	          // Wichtiger als die Breite ist die Hoehe (hier: 18),
	          // damit, ein leeres Feld genauso hoch ist wie eines
	          // mit Text
	          setPreferredSize(new Dimension(50,18));
	          
	          // Font-Zuweisung, damit nicht die beim
	          // Metal Look & Feel uebliche Fettschrift
	          // verwendet wird
	          setFont(new Font("Dialog", Font.PLAIN, 12));
    		}
		}
	} 