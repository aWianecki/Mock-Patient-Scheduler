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
import edu.miami.cis324.scheduler.xml.data.PatientImpl;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.utils.SchedulerReadWriteUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerWriterUtils;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
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
public class PatientAddFrame extends JInternalFrame {
    
    private static final String saveString = "Save";
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
    
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
 
    public PatientAddFrame(Scheduler scheduler, ObjectOutputStream oOut) {
    
        super("Patient Add", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        this.scheduler = scheduler;
        this.oOut = oOut;
        
        cal = new JDateChooser(Calendar.getInstance().getTime());
        
        fNameLabel = new JLabel(fNameString);
        lNameLabel = new JLabel(lNameString);
        ssnLabel = new JLabel(ssnString);
        birthLabel = new JLabel(birthString);
        
        fNameField = new JTextField();
        fNameField.setColumns(20);
        
        lNameField = new JTextField();
        
        ssnField = new JTextField();
        ssnField.setText("XXX-XX-XXXX");
                
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
        //...Create the GUI and put it in the window...
        
 
        //Create the list and put it in a scroll pane.
 
        //Create a panel that uses BoxLayout.
        
        //...Then set the window size or call pack...
        setSize(300,300);
 
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            
    		String ssn = ssnField.getText();
        	
        	Pattern ssnPattern = Pattern.compile("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$");
        	Matcher matcher = ssnPattern.matcher(ssn);
        	String firstName = fNameField.getText();
        	String lastName = lNameField.getText();
        	
        	if(matcher.matches() && !firstName.isEmpty() && !firstName.isEmpty()) {
        		String name = firstName + " " + lastName;
        		Date birthdate = cal.getDate();
        		Patient p = new PatientImpl(name, ssn, birthdate);
        		scheduler.addPatient(p);
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
        JFrame frame = new JFrame("Patient Add");
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
        JComponent newContentPane = new PatientAddFrame(scheduler, oOut);
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

