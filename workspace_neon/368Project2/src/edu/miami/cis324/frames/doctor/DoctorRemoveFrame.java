package edu.miami.cis324.frames.doctor;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import edu.miami.cis324.frames.visit.VisitViewListFrame;
import edu.miami.cis324.scheduler.xml.Packet;
import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.awt.*;
 
/* Used by InternalFrameDemo.java. */
public class DoctorRemoveFrame extends JInternalFrame implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private JList<Doctor> list;
    private DefaultListModel<Doctor> listModel;
    
    private static final String removeString = "Remove";
    private static final String editString = "Edit";
    private static final String visitsString = "All Visits";
    private static final String futureVisitsString = "Upcoming Visits";
    private JButton removeButton;
    private JButton editButton;
    private JButton visitsButton;
    private JButton futureVisitsButton;
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
    
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public DoctorRemoveFrame(Scheduler scheduler, ObjectOutputStream oOut) {
        super("Doctors", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
 
        //...Create the GUI and put it in the window...
        this.scheduler = scheduler;
        this.oOut = oOut;
        listModel = new DefaultListModel<Doctor>();
        
        fillList();
 
        //Create the list and put it in a scroll pane.
        list = new JList<Doctor>(listModel);
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
        
        visitsButton = new JButton(visitsString);
        visitsButton.setActionCommand(visitsString);
        visitsButton.addActionListener(new VisitsListener());
        
        futureVisitsButton = new JButton(futureVisitsString);
        futureVisitsButton.setActionCommand(futureVisitsString);
        futureVisitsButton.addActionListener(new FutureVisitsListener());
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(removeButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(editButton);
        buttonPane.add(visitsButton);
        buttonPane.add(futureVisitsButton);
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
            
            Doctor d = listModel.remove(index);
            d.remove();
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
            
            Doctor d = listModel.get(index);
            
            DoctorViewEditFrame pve= new DoctorViewEditFrame(scheduler, oOut, d);
            pve.setVisible(true);
            getParent().add(pve);
        }
    }
    
    class VisitsListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		int index = list.getSelectedIndex();
            if(index < 0) return;

    		Doctor d = listModel.get(index);
            
    		VisitViewListFrame VVADF = new VisitViewListFrame(scheduler, oOut, d.getDoctorID(), false, true);
    		VVADF.setVisible(true);
    		getParent().add(VVADF);
   	 }
   }
   
   class FutureVisitsListener implements ActionListener {
   	public void actionPerformed(ActionEvent e) {
   		int index = list.getSelectedIndex();
        if(index < 0) return;

   		Doctor d = listModel.get(index);
        
   		VisitViewListFrame VVFDF= new VisitViewListFrame(scheduler, oOut, d.getDoctorID(), false, false);
   		VVFDF.setVisible(true);
   		getParent().add(VVFDF);
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
 
    private void fillList() {
    	Map<Integer, Doctor> doctors = scheduler.getDoctors();
    	Iterator<Doctor> i = doctors.values().iterator();
    	while(i.hasNext()) {
    		Doctor d = i.next();
    		if(!d.isRemoved())
    			listModel.addElement(d);
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

