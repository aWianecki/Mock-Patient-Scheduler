package edu.miami.cis324.frames.patient;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.stream.XMLStreamException;

import edu.miami.cis324.frames.scheduler.SchedulerClient;
import edu.miami.cis324.scheduler.xml.Packet;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.utils.SchedulerReadWriteUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerWriterUtils;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import com.toedter.*;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

/* Used by InternalFrameDemo.java. */
public class PatientViewEditFrame extends JInternalFrame {
    
    private static final String saveString = "Save";
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
	private static Patient p;
    
	private JButton saveButton;
	private JDateChooser cal;
	
	private JLabel fNameLabel;
	private JLabel lNameLabel;
	private JLabel ssnLabel;
	private JLabel birthLabel;
	
	private static String fNameString = "First Name: ";
	private static String lNameString = "Last Name: ";
	private static String ssnString = "SSN: ";
	private static String birthString = "BirthDate: ";
	
	private JTextField ssnField;
	private JTextField fNameField;
	private JTextField lNameField;
	//	private JDatePicker birthPicker;
	
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public PatientViewEditFrame(Scheduler scheduler, ObjectOutputStream oOut, Patient p) {
    
        super("Patient Edit", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        this.p = p;
        this.scheduler = scheduler;
        this.oOut = oOut;
        
        cal = new JDateChooser(p.getBirthdate());
        
        fNameLabel = new JLabel(fNameString);
        lNameLabel = new JLabel(lNameString);
        ssnLabel = new JLabel(ssnString);
        birthLabel = new JLabel(birthString);
        
        fNameField = new JTextField();
        fNameField.setText(p.getFirstName());
        fNameField.setColumns(20);
        
        lNameField = new JTextField();
        lNameField.setText(p.getLastName());
        
        ssnField = new JTextField();
        ssnField.setText(p.getSSN());
                
        fNameLabel.setLabelFor(fNameField);
        lNameLabel.setLabelFor(lNameField);
        ssnLabel.setLabelFor(ssnField);
        
        saveButton = new JButton(saveString);
        saveButton.setActionCommand(saveString);
        saveButton.addActionListener(new SaveListener());
        
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(fNameLabel);
        labelPane.add(lNameLabel);
        labelPane.add(ssnLabel);
        labelPane.add(birthLabel);
        
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(fNameField);
        fieldPane.add(lNameField);
        fieldPane.add(ssnField);
        fieldPane.add(cal);
        
        add(labelPane, BorderLayout.WEST);
        add(fieldPane, BorderLayout.EAST);
        add(saveButton, BorderLayout.PAGE_END);
        
        setSize(300,300);
 
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            
        	Pattern ssn = Pattern.compile("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$");
        	Matcher matcher = ssn.matcher(ssnField.getText());
        	
        	if(matcher.matches()) {
        		p.setFirstName(fNameField.getText());
        		p.setLastName(lNameField.getText());
        		p.setSSN(ssnField.getText());
        		p.setBirthDate(cal.getDate());
        		write();
        		saveButton.setEnabled(false);
        	}
        	else
        		ssnField.setText("Invalid SSN");
        }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Patient Edit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		write();
        		frame.setVisible(false);
        		frame.dispose();
        	}
        });
        
        //Create and set up the content pane.
        JComponent newContentPane = new PatientViewEditFrame(scheduler, oOut, p);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void write() {
    	Packet pkt = new Packet(scheduler, "WRITE");
    	try {
    		oOut.writeObject(pkt);
    	} catch (IOException e) {
    		System.out.println("Failed to write: " + e);
    		System.exit(1);
    	}
    }
}

