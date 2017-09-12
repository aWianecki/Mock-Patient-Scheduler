package edu.miami.cis324.scheduler.xml;

import java.io.Serializable;

import edu.miami.cis324.scheduler.xml.data.Scheduler;

public class Packet implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;
	private Scheduler scheduler;
	
	public Packet() {
		type = null;
		scheduler = null;
	}
	
	public Packet(Scheduler scheduler, String type) {
		this.type = type;
		this.scheduler = scheduler;
	}
	
	public String getType() {
		return type;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
}
