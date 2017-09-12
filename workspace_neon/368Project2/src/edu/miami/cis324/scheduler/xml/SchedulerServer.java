package edu.miami.cis324.scheduler.xml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.stream.XMLStreamException;

import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.utils.SchedulerReadWriteUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerReaderUtils;
import edu.miami.cis324.scheduler.xml.utils.SchedulerWriterUtils;

public class SchedulerServer {

	private int port;
	private ArrayList<ClientThread> clients;
	private Scheduler scheduler;
	private boolean go;
	
	public SchedulerServer() {
		port = 5000;
		clients = new ArrayList<ClientThread>();
		go = true;
		try {
			scheduler = SchedulerReaderUtils.readScheduler(SchedulerReadWriteUtils.FILE);
		} catch (XMLStreamException e) {
			System.out.println("XML unable to be read: \n" + e);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("File not found: \n" + e);
			System.exit(1);
		}
		start();
	}
	
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(go) {
				System.out.println("Server waiting for Clients on port " + port + ".");
				Socket socket = serverSocket.accept();
				if(!go) {
					break;
				}
				ClientThread t = new ClientThread(socket);
				clients.add(t);
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < clients.size(); ++i) {
					ClientThread temp = clients.get(i);
					try {
						temp.oIn.close();
						temp.oOut.close();
						temp.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				System.out.println("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) {
            String msg = " Exception on new ServerSocket: " + e + "\n";
			System.out.println(msg);
		}
		
	}

	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream oIn;
		ObjectOutputStream oOut;
		
		ClientThread(Socket socket) {
			this.socket = socket;
			try {
				oOut = new ObjectOutputStream(socket.getOutputStream());
				oIn  = new ObjectInputStream(socket.getInputStream());
				Packet pkt = new Packet(scheduler, "ACK");
				oOut.writeObject(pkt);
			} catch (IOException e) {
				System.out.println("Failed to create new Input/output Streams: " + e);
				return;
			}
		}
		
		public void run() {
			boolean keepGoing = true;
			Packet pkt;
			while(keepGoing) {
				try {
					 pkt = (Packet)oIn.readObject();
				} catch (IOException e) {
					System.out.println(" Exception reading: " + e);
					break;				
				} catch(ClassNotFoundException e2) {
					break;
				}
				if(pkt.getType().equals("READ")) {
					try {
						scheduler = SchedulerReaderUtils.readScheduler(SchedulerReadWriteUtils.FILE);
					} catch (XMLStreamException e) {
						System.out.println("XML unable to be read: \n" + e);
						System.exit(1);
					} catch (IOException e) {
						System.out.println("File not found: \n" + e);
						System.exit(1);
					}
					
					try {
						oOut.writeObject(new Packet(scheduler,"ACK"));
					} catch (IOException e) {
						System.out.println("Failed to Send: " + e);
						keepGoing = false;
					}
				}
				
				else if(pkt.getType().equals("LOGOUT")){
					keepGoing = false;
					go = false;
				}
				
				else if(pkt.getType().equals("WRITE")) {
					scheduler = pkt.getScheduler();
					try {
						SchedulerWriterUtils.writeScheduler(SchedulerReadWriteUtils.FILE, scheduler);
					} catch (XMLStreamException e) {
						System.out.println("XML unable to be read: \n" + e);
						System.exit(1);
					} catch (IOException e) {
						System.out.println("File not found: \n" + e);
						System.exit(1);
					}
				}
				
				else {
					continue;
				}
			}
			clients.remove(this);
			close();
		}
		
		public void close() {
			try {
				if(oOut != null) oOut.close();
				if(oIn != null) oIn.close();
				if(socket != null) socket.close();
			} catch (Exception e) {}
		}
	}
	
	public static void main(String[] args) {
		SchedulerServer server = new SchedulerServer();
	}
	
}
