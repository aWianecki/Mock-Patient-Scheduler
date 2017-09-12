package edu.miami.cis324.frames.visit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
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

import edu.miami.cis324.frames.visit.VisitViewListFrame;
import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import java.awt.event.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.awt.*;
 
/* Used by InternalFrameDemo.java. */
public class VisitAddDoctorFrame extends JInternalFrame implements ListSelectionListener {
    private JList list;
    private DefaultListModel<Doctor> listModel;
    
    private static final String selectString = "Select";
    private JButton selectButton;
	private static Scheduler scheduler;
	private static int patientID;
    
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public VisitAddDoctorFrame(Scheduler scheduler, int patientID) {
        super("Doctors", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        //...Create the GUI and put it in the window...
        this.scheduler = scheduler;
        this.patientID = patientID;
        listModel = new DefaultListModel<Doctor>();
        
        fillList();
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        selectButton = new JButton(selectString);
        selectButton.setActionCommand(selectString);
        selectButton.addActionListener(new SelectListener());
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(selectButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        
        //...Then set the window size or call pack...
        setSize(300,300);
 
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    class SelectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            if(index < 0) return;
            
            Doctor d = listModel.get(index);
            
            VisitAddTimeFrame vatf= new VisitAddTimeFrame(scheduler, patientID, d.getDoctorID());
            vatf.setVisible(true);
            getParent().add(vatf);
            
            selectButton.setEnabled(false);
        }
    }
    
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
 
            if (list.getSelectedIndex() == -1) {
            //No selection, disable remove button.
            	selectButton.setEnabled(false);
 
            } else {
            //Selection, enable the remove button.
            	selectButton.setEnabled(true);
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
        JFrame frame = new JFrame("ListDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		frame.setVisible(false);
        		frame.dispose();
        	}
        });
        
        //Create and set up the content pane.
        JComponent newContentPane = new VisitAddDoctorFrame(scheduler, patientID);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private void fillList() {
    	Map<Integer, Doctor> doctors = scheduler.getDoctors();
    	Iterator<Doctor> i = doctors.values().iterator();
    	while(i.hasNext()) {
    		Doctor d = i.next();
    		if(!d.isRemoved())
    			listModel.addElement(d);
    	}
    }
}

