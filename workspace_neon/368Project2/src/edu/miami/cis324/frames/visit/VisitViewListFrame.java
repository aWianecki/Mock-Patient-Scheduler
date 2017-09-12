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

import edu.miami.cis324.frames.scheduler.SchedulerClient;
import edu.miami.cis324.scheduler.xml.Packet;
import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.data.Visit;
import edu.miami.cis324.scheduler.xml.utils.SchedulerReadWriteUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerWriterUtils;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.*;
 
/* Used by InternalFrameDemo.java. */
public class VisitViewListFrame extends JInternalFrame implements ListSelectionListener {
    private JList list;
    private DefaultListModel<Visit> listModel;
    
    private static final String removeString = "Delete";
    private static final String editString = "Edit";
    private JButton removeButton;
    private JButton editButton;
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
	private static boolean all;
    
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public VisitViewListFrame(Scheduler scheduler, ObjectOutputStream oOut, boolean all) {
        super("Visits", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        //...Create the GUI and put it in the window...
        this.scheduler = scheduler;
        this.all = all;
        this.oOut = oOut;
        listModel = new DefaultListModel<Visit>();
        
        fillList(all);
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        removeButton = new JButton(removeString);
        removeButton.setActionCommand(removeString);
        removeButton.addActionListener(new RemoveListener());
        
        editButton = new JButton(editString);
        editButton.setActionCommand(editString);
        editButton.addActionListener(new EditListener());
        
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(removeButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(editButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        
        //...Then set the window size or call pack...
        setSize(300,300);
 
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    public VisitViewListFrame(Scheduler scheduler, ObjectOutputStream oOut, int id, boolean patient, boolean all) {
        super("Visits", 
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable
   
          //...Create the GUI and put it in the window...
          this.scheduler = scheduler;
          this.oOut = oOut;
          listModel = new DefaultListModel<Visit>();
          
          fillList(id, patient, all);
   
          //Create the list and put it in a scroll pane.
          list = new JList(listModel);
          list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          list.setSelectedIndex(0);
          list.addListSelectionListener(this);
          list.setVisibleRowCount(5);
          JScrollPane listScrollPane = new JScrollPane(list);
   
          removeButton = new JButton(removeString);
          removeButton.setActionCommand(removeString);
          removeButton.addActionListener(new RemoveListener());
          
          editButton = new JButton(editString);
          editButton.setActionCommand(editString);
          editButton.addActionListener(new EditListener());
          
   
          //Create a panel that uses BoxLayout.
          JPanel buttonPane = new JPanel();
          buttonPane.setLayout(new BoxLayout(buttonPane,
                                             BoxLayout.LINE_AXIS));
          buttonPane.add(removeButton);
          buttonPane.add(Box.createHorizontalStrut(5));
          buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
          buttonPane.add(Box.createHorizontalStrut(5));
          buttonPane.add(editButton);
          buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   
          add(listScrollPane, BorderLayout.CENTER);
          add(buttonPane, BorderLayout.PAGE_END);
          
          //...Then set the window size or call pack...
          setSize(300,300);
   
          //Set the window's location.
          setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
    
    class RemoveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            
            if(index < 0) return;
            
            Visit v = listModel.remove(index);
            scheduler.getVisits().remove(v);
            write();
 
            int size = listModel.getSize();
 
            if (size == 0) { //Nobody's left, disable firing.
                removeButton.setEnabled(false);
 
            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }
 
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }
 
    class EditListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            if(index < 0) return;

            Visit v = listModel.get(index);
           	if(v.getDaysTilVisit() >= 0) {
           		VisitEditFrame vef= new VisitEditFrame(scheduler, oOut, v);
           		vef.setVisible(true);
           		getParent().add(vef);
        	}
        }
    }
    
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
 
            if (list.getSelectedIndex() == -1) {
            //No selection, disable remove button.
                removeButton.setEnabled(false);
 
            } else {
            //Selection, enable the remove button.
                removeButton.setEnabled(true);
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
        JFrame frame = new JFrame("Visits");
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
        JComponent newContentPane = new VisitViewListFrame(scheduler, oOut, all);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private void fillList(boolean all) {
    	List<Visit<Integer, Integer>> visits = scheduler.getVisits();
    	Map<Integer, Patient> patients = scheduler.getPatients();
    	Map<Integer, Doctor> doctors = scheduler.getDoctors();
    	
    	Iterator<Visit<Integer, Integer>> i = visits.iterator();
    	while(i.hasNext()) {
    		Visit<Integer, Integer> v = i.next();
    		Patient p = patients.get(v.getVisitor());
    		Doctor d = doctors.get(v.getHost());
    		if(!d.isRemoved() && d != null && !p.isRemoved() && p != null && (all || v.getDaysTilVisit() >= 0))
    			listModel.addElement(v);
    	}
    }
    
    private void fillList(int id, boolean patient, boolean all) {
    	List<Visit<Integer, Integer>> visits = scheduler.getVisits();
    	Map<Integer, Patient> patients = scheduler.getPatients();
    	Map<Integer, Doctor> doctors = scheduler.getDoctors();
    	
    	Iterator<Visit<Integer, Integer>> i = visits.iterator();
    	while(i.hasNext()) {
    		int thisID;
    		Visit<Integer, Integer> v = i.next();
    		Patient p = patients.get(v.getVisitor());
    		Doctor d = doctors.get(v.getHost());
    		
    		if(!d.isRemoved() && d != null && !p.isRemoved() && p != null) {
    			if(patient) {
    				thisID = v.getVisitor();
    			}
    			
    			else {
    				thisID = v.getHost();
    			}
    			
    			if(thisID == id && (all || v.getDaysTilVisit() >= 0)) {
						listModel.addElement(v);
				}
    		}
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

