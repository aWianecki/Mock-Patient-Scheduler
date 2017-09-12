package edu.miami.cis324.frames.visit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.DoctorImpl;
import edu.miami.cis324.scheduler.xml.data.MedicalSpecialty;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.PatientImpl;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.data.Visit;
import edu.miami.cis324.scheduler.xml.data.VisitImpl;
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
public class VisitEditFrame extends JInternalFrame {
    
    private static final String saveString = "Save";
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
	private static Visit<Integer, Integer> v;
    
	private JButton saveButton;
	private JDateChooser cal;
	private JTextField time;
	
	private JLabel dateLabel;
	private JLabel timeLabel;
	
	private static String dateString = "Date: ";
	private static String timeString = "Time (24hr): ";
	
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public VisitEditFrame(Scheduler scheduler, ObjectOutputStream oOut, Visit<Integer, Integer> v) {
    
        super("Visit Edit", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        this.scheduler = scheduler;
        this.v = v;
        this.oOut = oOut;
        Date visitDate = v.getVisitDate();
        
        cal = new JDateChooser(visitDate);
        time = new JTextField(20);
        time.setText(visitDate.getHours() + ":" + visitDate.getMinutes());
        
        dateLabel = new JLabel(dateString);
        timeLabel = new JLabel(timeString);
        
        saveButton = new JButton(saveString);
        saveButton.setActionCommand(saveString);
        saveButton.addActionListener(new SaveListener());
        
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(dateLabel);
        labelPane.add(timeLabel);
        
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(cal);
        fieldPane.add(time);
        
        add(labelPane, BorderLayout.WEST);
        add(fieldPane, BorderLayout.EAST);
        add(saveButton, BorderLayout.SOUTH);

        setSize(600,300);
 
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            
        	String visitTime = time.getText();
            Pattern timePattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        	Matcher matcher = timePattern.matcher(visitTime);
        	
        	if(matcher.matches()) {
        		String[] temp = visitTime.split(":");
        		int hour = Integer.parseInt(temp[0]);
        		int min = Integer.parseInt(temp[1]);
        		Date visitDate = cal.getDate();
        		
        		Date oldDate = v.getVisitDate();
        		oldDate.setDate(visitDate.getDate());
        		oldDate.setMonth(visitDate.getMonth());
        		oldDate.setYear(visitDate.getYear());
        		oldDate.setHours(hour);
        		oldDate.setMinutes(min);
        		write();
        		saveButton.setEnabled(false);
        	}
        	else {
        		time.setText("Invalid Time");
        	}
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
        JComponent newContentPane = new VisitEditFrame(scheduler, oOut, v);
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

