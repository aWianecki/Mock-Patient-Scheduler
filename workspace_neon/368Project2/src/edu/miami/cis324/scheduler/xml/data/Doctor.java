package edu.miami.cis324.scheduler.xml.data;

import java.util.Date;

public interface Doctor extends Comparable<Doctor>{
	public PersonalData getData();
	public int getDoctorID();
	public String getLastName();
	public String getFirstName();
	public MedicalSpecialty getMedicalSpecialty();
	public int compareTo(Doctor anotherDoctor);
	public Date getBirthdate();
	public String getSSN();
	public Name getName();
	public boolean remove();
	public boolean unremove();
	public boolean isRemoved();
	public void setFirstName(String firstName);
	public void setLastName(String lastName);
	public void setSSN(String ssn);
	public void setBirthDate(Date birthDate);
	public void setSpecialty(MedicalSpecialty specialty);
}