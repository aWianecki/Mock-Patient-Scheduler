package edu.miami.cis324.scheduler.xml.data;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

public class VisitImpl<V,T> implements Visit<V,T>, Serializable{
	private V visitor;
	private T host;
	private Date visitDate;
	
	public VisitImpl() {
		visitor = null;
		host = null;
		visitDate = null;
	}
	
	public VisitImpl(V visitor, T host, Date visitDate) {
		this.visitor = visitor;
		this.host = host;
		this.visitDate = visitDate;
	}
	
	public long getDaysTilVisit() {
		long days=0;
		GregorianCalendar cal1 = new GregorianCalendar();
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(visitDate);
		long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();
		GregorianCalendar cal3 = new GregorianCalendar();
		cal3.setTimeInMillis(diff);
		long numberOfMilliSecondsInADay = 1000*60*60*24;
		days = cal3.getTimeInMillis() / numberOfMilliSecondsInADay;
		return days;
	}

	public V getVisitor() {
		return visitor;
	}
	
	public T getHost() {
		return host;
	}
	
	public Date getVisitDate() {
		return visitDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!VisitImpl.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		final VisitImpl<?,?> temp = (VisitImpl<?,?>) obj;
		
		if(this.visitor.equals(temp.visitor) && this.host.equals(temp.host) && this.visitDate.equals(temp.visitDate)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (visitor != null ? visitor.hashCode() : 0);
		hash = 53 * hash + (host != null ? host.hashCode() : 0);
		hash = 53 * hash + (visitDate != null ? visitDate.hashCode() : 0);
		return hash;
	}
	
	@Override
	public String toString() {
		return "Pat ID: " + visitor + " Doc ID: " + host + " Date: " + visitDate;
	}
}