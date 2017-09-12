package edu.miami.cis324.scheduler.xml.data;

import java.util.Date;

public interface Visit<V,T> {
	public V getVisitor();
	public T getHost();
	public Date getVisitDate();
	public long getDaysTilVisit();
}
