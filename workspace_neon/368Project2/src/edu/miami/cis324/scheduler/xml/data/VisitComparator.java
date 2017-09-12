package edu.miami.cis324.scheduler.xml.data;

import java.util.Comparator;

public class VisitComparator implements Comparator<Visit<?,?>> {
	public int compare(Visit<?,?> v1, Visit<?,?> v2) {
		return v1.getVisitDate().compareTo(v2.getVisitDate());
	}
}
