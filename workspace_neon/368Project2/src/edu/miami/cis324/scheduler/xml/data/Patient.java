package edu.miami.cis324.scheduler.xml.data;

import java.util.Date;

public interface Patient extends Comparable<Patient>{
	public int getPatientID();
	public String getLastName();
	public String getFirstName();
	public String getSSN();
	public Date getBirthdate();
	public PersonalData getData();
	public int getAge();
	public int compareTo(Patient anotherPatient);
	public Name getName();
	public boolean remove();
	public boolean unremove();
	public boolean isRemoved();
	public void setFirstName(String firstName);
	public void setLastName(String lastName);
	public void setSSN(String ssn);
	public void setBirthDate(Date birthDate);
}
