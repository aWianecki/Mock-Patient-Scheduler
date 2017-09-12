package edu.miami.cis324.frames.scheduler;

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.xml.stream.XMLStreamException;

import edu.miami.cis324.frames.patient.*;
import edu.miami.cis324.frames.doctor.*;
import edu.miami.cis324.frames.visit.*;
import edu.miami.cis324.scheduler.xml.Packet;
import edu.miami.cis324.scheduler.xml.data.Scheduler;

import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
 
/*
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */

public class SchedulerClient extends JFrame implements ActionListener {
    JDesktopPane desktop;
    static Socket socket;
    static ObjectInputStream oIn;
    static ObjectOutputStream oOut;
    String host = "52.37.168.22";
    int port = 5000;
    public static Scheduler scheduler;
    
 
    public SchedulerClient() {
        super("Scheduler");
 
        login();
        System.out.println("logged in");
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
 
        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);
        setJMenuBar(createMenuBar());
 
        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
    }
 
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
 
        //Set up the lone menu.
 
        JMenu pMenu = new JMenu("Patients");
        menuBar.add(pMenu);
        
        JMenuItem menuItemPA = new JMenuItem(FrameUtils.PA.substring(1));
        menuItemPA.setActionCommand(FrameUtils.PA);
        menuItemPA.addActionListener(this);
        pMenu.add(menuItemPA);
               
        JMenuItem menuItemPD = new JMenuItem(FrameUtils.PD.substring(1));
        menuItemPD.setActionCommand(FrameUtils.PD);
        menuItemPD.addActionListener(this);
        pMenu.add(menuItemPD);
        
        JMenuItem menuItemPU = new JMenuItem(FrameUtils.PU.substring(1));
        menuItemPU.setActionCommand(FrameUtils.PU);
        menuItemPU.addActionListener(this);
        pMenu.add(menuItemPU);
        
        JMenu dMenu = new JMenu("Doctors");
        menuBar.add(dMenu);
        
        JMenuItem menuItemDA = new JMenuItem(FrameUtils.DA.substring(1));
        menuItemDA.setActionCommand(FrameUtils.DA);
        menuItemDA.addActionListener(this);
        dMenu.add(menuItemDA);
        
        JMenuItem menuItemDD = new JMenuItem(FrameUtils.DD.substring(1));
        menuItemDD.setActionCommand(FrameUtils.DD);
        menuItemDD.addActionListener(this);
        dMenu.add(menuItemDD);
        
        JMenuItem menuItemDU = new JMenuItem(FrameUtils.DU.substring(1));
        menuItemDU.setActionCommand(FrameUtils.DU);
        menuItemDU.addActionListener(this);
        dMenu.add(menuItemDU);
 
        JMenu vMenu = new JMenu("Visits");
        menuBar.add(vMenu);
        
        JMenuItem menuItemVVL = new JMenuItem(FrameUtils.VVL.substring(1));
        menuItemVVL.setActionCommand(FrameUtils.VVL);
        menuItemVVL.addActionListener(this);
        vMenu.add(menuItemVVL);
        
        JMenuItem menuItemVVU = new JMenuItem(FrameUtils.VVU.substring(1));
        menuItemVVU.setActionCommand(FrameUtils.VVU);
        menuItemVVU.addActionListener(this);
        vMenu.add(menuItemVVU);
        
        JMenuItem menuItemVA = new JMenuItem(FrameUtils.VA.substring(1));
        menuItemVA.setActionCommand(FrameUtils.VA);
        menuItemVA.addActionListener(this);
        vMenu.add(menuItemVA);
        
        
        return menuBar;
    }
 
    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
    	createFrame(e.getActionCommand());
    }
 
    //Create a new internal frame.
    protected void createFrame(String command) {
        JInternalFrame frame = null;
        request();
        switch (command) {
        	case FrameUtils.PA:		frame = new PatientAddFrame(scheduler, oOut);
									break;
        	case FrameUtils.PD:		frame = new PatientRemoveFrame(scheduler, oOut);
									break;
        	case FrameUtils.PU:		frame = new PatientRestoreFrame(scheduler, oOut);
									break;
			case FrameUtils.DA:		frame = new DoctorAddFrame(scheduler, oOut);
									break;
			case FrameUtils.DD:		frame = new DoctorRemoveFrame(scheduler, oOut);
									break;
			case FrameUtils.DU:		frame = new DoctorRestoreFrame(scheduler, oOut);
									break;
        	case FrameUtils.VA:		frame = new VisitAddPatientFrame(scheduler);	
									break;
        	case FrameUtils.VVL:	frame = new VisitViewListFrame(scheduler, oOut, true);	
									break;
        	case FrameUtils.VVU:	frame = new VisitViewListFrame(scheduler, oOut, false);	
									break;
			default :				logout();
									break;
        }
        
        frame.setVisible(true);
        frame.setSize(600, 300);
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
 
    //Quit the application.
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        SchedulerClient frame = new SchedulerClient();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		frame.setVisible(false);
        		frame.dispose();
        		logout();
        	}
        });
        //Display the window.
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    public void login() {
    	System.out.println("logging in");
    	try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.out.println("Host not found: " + e);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Failed to connect: " + e);
			System.exit(1);
		}
    	System.out.println("connected");
    	try {
			oIn = new ObjectInputStream(socket.getInputStream());
			oOut = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error setting up streams: " + e);
			System.exit(1);
		}
    	
    	get();
    }
    
    public static void logout() {
    	write();
    	Packet pkt = new Packet(null, "LOGOUT");
    	try {
			oOut.writeObject(pkt);
		} catch (IOException e) {
			System.out.println("Failed to write: " + e);
			System.exit(1);
		}
    	try {
			if(socket != null) socket.close();
			if(socket != null) oIn.close();
			if(socket != null) oOut.close();
		} catch (IOException e) {
			System.out.println("Failed to close: " + e);
			System.exit(1);
		}
    	
    	System.exit(0);
    }
    
    public void request() {
    	Packet pkt = new Packet(null, "READ");
    	try {
			oOut.writeObject(pkt);
		} catch (IOException e) {
			System.out.println("Error sending packet: " + e);
			System.exit(1);
		}
    	get();
    	
    }
    
    public void get() {
    	try {
    		Packet pkt = new Packet();
    		Object obj = oIn.readObject();
    		pkt = (Packet)obj;
    		if(pkt.getType().equals("ACK"))
    			scheduler = pkt.getScheduler();
    		else {
    			System.out.println("Received Bad Packet");
    			System.exit(1);
    		}
		} catch (ClassNotFoundException e) {
			System.out.println("Error converting to Packet: " + e);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Packet: " + e);
			System.exit(1);
			e.printStackTrace();
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
