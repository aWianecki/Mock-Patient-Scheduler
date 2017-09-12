package edu.miami.cis324.frames.doctor;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import edu.miami.cis324.scheduler.xml.Packet;
import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.DoctorImpl;
import edu.miami.cis324.scheduler.xml.data.MedicalSpecialty;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

/* Used by InternalFrameDemo.java. */
public class DoctorAddFrame extends JInternalFrame {
	private static final long serialVersionUID = 1L;
	private static final String saveString = "Save";
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
    
	private JButton saveButton;
	private JDateChooser cal;
	private JComboBox<MedicalSpecialty> specialty;
	
	private JLabel fNameLabel;
	private JLabel lNameLabel;
	private JLabel ssnLabel;
	private JLabel birthLabel;
	private JLabel specialtyLabel;
	
	private static String fNameString = "First Name: ";
	private static String lNameString = "Last Name: ";
	private static String ssnString = "SSN: ";
	private static String birthString = "BirthDate: ";
	private static String specialtyString = "Specialty: ";
	
	private JTextField ssnField;
	private JTextField fNameField;
	private JTextField lNameField;
	//	private JDatePicker birthPicker;
	
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public DoctorAddFrame(Scheduler scheduler, ObjectOutputStream oOut) {
    
        super("Doctor Add", 
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
        specialtyLabel = new JLabel(specialtyString);
        
        fNameField = new JTextField();
        fNameField.setColumns(20);
        
        lNameField = new JTextField();
        
        ssnField = new JTextField();
        ssnField.setText("XXX-XX-XXXX");
                
        fNameLabel.setLabelFor(fNameField);
        lNameLabel.setLabelFor(lNameField);
        ssnLabel.setLabelFor(ssnField);
        
        specialty = new JComboBox<MedicalSpecialty>(MedicalSpecialty.values());
        specialty.setSelectedIndex(specialty.getItemCount()-1);
        
        saveButton = new JButton(saveString);
        saveButton.setActionCommand(saveString);
        saveButton.addActionListener(new SaveListener());
        
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(fNameLabel);
        labelPane.add(lNameLabel);
        labelPane.add(ssnLabel);
        labelPane.add(birthLabel);
        labelPane.add(specialtyLabel);
        
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(fNameField);
        fieldPane.add(lNameField);
        fieldPane.add(ssnField);
        fieldPane.add(cal);
        fieldPane.add(specialty);
        
        add(labelPane, BorderLayout.WEST);
        add(fieldPane, BorderLayout.EAST);
        add(saveButton, BorderLayout.SOUTH);
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
        		MedicalSpecialty ms = (MedicalSpecialty)specialty.getSelectedItem();
        		Doctor d = new DoctorImpl(name, ssn, birthdate, ms);
        		scheduler.addDoctor(d);
        		write();
        		saveButton.setEnabled(false);
        	}
        	else
        		ssnField.setText("Invalid SSN");
        }
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

