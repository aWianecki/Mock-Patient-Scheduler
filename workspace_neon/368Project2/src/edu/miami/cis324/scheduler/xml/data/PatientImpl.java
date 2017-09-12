package edu.miami.cis324.scheduler.xml.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class PatientImpl implements Patient, Serializable{
	private static int pID = 1;
	private int patientID;
	private Name name;
	private PersonalData data;
	
	public PatientImpl() {
		patientID = 0;
		name = new Name("");
		data = new PersonalData(null, "XXX-XX-XXXX");
	}
	
	public PatientImpl(String fullName, String SSN, Date birthDate) {
		patientID = pID;
		pID++;
		name = new Name(fullName);
		data = new PersonalData(birthDate, SSN);
	}
	
	public PatientImpl(Name name, PersonalData data) {
		patientID = pID;
		pID++;
		this.name = name;
		this.data = data;
	}
	
	public PatientImpl(Name name, PersonalData data, int patientID) {
		this.patientID = patientID;
		if(Math.abs(patientID) >= pID) {
			pID = patientID + 1;
		}
		this.name = name;
		this.data = data;
	}
	
	public PersonalData getData() {
		return data;
	}
	
	public Name getName() {
		return name;
	}
	
	public int getPatientID() {
		return patientID;
	}
	
	public String getLastName() {
		return name.getLastName();
	}
	
	public String getFirstName() {
		return name.getFirstName();
	}
	
	public String getSSN() {
		return data.getSsn();
	}
	
	public Date getBirthdate() {
		return data.getBirthdate();
	}
	
	public int getAge() {
		Calendar dob = Calendar.getInstance();
		dob.setTime(data.getBirthdate());
		
		Calendar today = Calendar.getInstance();
		
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		
		if(today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}
		
		return age;
	}
	
	public void setFirstName(String firstName) {
		name.setFirstName(firstName);
	}
	
	public void setLastName(String lastName) {
		name.setLastName(lastName);
	}
	
	public void setSSN(String ssn) {
		data.setSsn(ssn);
	}
	
	public void setBirthDate(Date birthDate) {
		data.setBirthdate(birthDate);
	}
	
	public boolean remove() {
		if(patientID > 0) {
			patientID *= -1;
			return true;
		}
		else return false;
	}
	
	public boolean unremove() {
		if(patientID < 0) {
			patientID *= -1;
			return true;
		}
		else return false;
	}
	
	public boolean isRemoved() {
		if(patientID < 0)
			return true;
		else return false;
	}
	
	@Override
	public String toString() {
		return name.toString() + "     ID: " + patientID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!PatientImpl.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		final PatientImpl temp = (PatientImpl) obj;
		
		if(this.patientID == temp.patientID && this.name.equals(temp.name) && this.data.equals(temp.data)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (name != null ? name.hashCode() : 0);
		hash = 53 * hash + (data != null ? data.hashCode() : 0);
		hash = 53 * hash + patientID;
		return hash;
	}
	
	public int compareTo(Patient anotherPatient) {
		int compare = this.name.compareTo(anotherPatient.getName());
		if (compare != 0) {
			return compare;
		}
		
		compare = this.data.compareTo(anotherPatient.getData());
		if (compare != 0) {
			return compare;
		}

		return 0;
	}
}
