package edu.miami.cis324.frames.doctor;

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
import edu.miami.cis324.scheduler.xml.utils.SchedulerReadWriteUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerWriterUtils;

import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.awt.*;
 
/* Used by InternalFrameDemo.java. */
public class DoctorRestoreFrame extends JInternalFrame implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private JList<Doctor> list;
    private DefaultListModel<Doctor> listModel;
    
    private static final String removeString = "Restore";
    private JButton restoreButton;
	private static Scheduler scheduler;
	private static ObjectOutputStream oOut;
    
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
 
    public DoctorRestoreFrame(Scheduler scheduler, ObjectOutputStream oOut) {
        super("Doctor Restore", 
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
 
        restoreButton = new JButton(removeString);
        restoreButton.setActionCommand(removeString);
        restoreButton.addActionListener(new RestoreListener());
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(restoreButton);
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
    
    class RestoreListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            if(index < 0) return;
            
            Doctor d = listModel.remove(index);
            d.unremove();
            write();            
 
            int size = listModel.getSize();
 
            if (size == 0) { //Nobody's left, disable firing.
            	restoreButton.setEnabled(false);
 
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
 
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
 
            if (list.getSelectedIndex() == -1) {
            //No selection, disable remove button.
            	restoreButton.setEnabled(false);
 
            } else {
            //Selection, enable the remove button.
            	restoreButton.setEnabled(true);
            }
        }
    }
 
    private void fillList() {
    	Map<Integer, Doctor> doctors = scheduler.getDoctors();
    	Iterator<Doctor> i = doctors.values().iterator();
    	while(i.hasNext()) {
    		Doctor d = i.next();
    		if(d.isRemoved())
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

