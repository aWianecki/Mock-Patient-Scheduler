package edu.miami.cis324.scheduler.xml.data;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DoctorImpl implements Doctor, Serializable {
	private static int dID = 1;
	private int doctorID;
	private Name name;
	private MedicalSpecialty medicalSpecialty;
	private PersonalData data;
	
	public DoctorImpl() {
		doctorID = 0;
		name = new Name("", "");
		medicalSpecialty = null;
		data= new PersonalData(null, "XXX-XX-XXXX");
	}
	
	public DoctorImpl(String fullName, String SSN, Date birthDate, MedicalSpecialty medicalSpecialty) {
		doctorID = dID;
		dID++;
		name = new Name(fullName);
		data = new PersonalData(birthDate, SSN);
		this.medicalSpecialty = medicalSpecialty;
	}
	
	public DoctorImpl(Name name, PersonalData data, MedicalSpecialty medicalSpecialty) {
		doctorID = dID;
		dID++;
		this.name = name;
		this.data = data;
		this.medicalSpecialty = medicalSpecialty;
	}
	
	public DoctorImpl(Name name, PersonalData data, MedicalSpecialty medicalSpecialty, int doctorID) {
		this.doctorID = doctorID;
		if(Math.abs(doctorID) >= dID) {
			dID = doctorID + 1;
		}
		this.name = name;
		this.data = data;
		this.medicalSpecialty = medicalSpecialty;
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
	
	public PersonalData getData() {
		return data;
	}
	
	public Date getBirthdate() {
		return data.getBirthdate();
	}
	
	public String getSSN() {
		return data.getSsn();
	}
	
	public int compareTo(Doctor anotherDoctor) {
		int compare = this.name.compareTo(anotherDoctor.getName());
		if (compare != 0) {
			return compare;
		}
		
		compare = this.data.compareTo(anotherDoctor.getData());
		if (compare != 0) {
			return compare;
		}

		return 0;
	}
	
	public int getDoctorID() {
		return doctorID;
	}
	
	public String getLastName() {
		return name.getLastName();
	}
	
	public String getFirstName() {
		return name.getFirstName();
	}
	
	public Name getName() {
		return name;
	}
	
	public MedicalSpecialty getMedicalSpecialty() {
		return medicalSpecialty;
	}
	
	public void setFirstName(String firstName) {
		name.setFirstName(firstName);
	}
	
	public void setLastName(String lastName) {
		name.setLastName(lastName);
	}
	
	public void setBirthDate(Date birthdate) {
		data.setBirthdate(birthdate);
	}
	
	public void setSpecialty(MedicalSpecialty medicalSpecialty) {
		this.medicalSpecialty = medicalSpecialty;
	}
	
	public void setSSN(String ssn) {
		data.setSsn(ssn);
	}

	@Override
	public String toString() {
		return name.toString() + "     ID: " + doctorID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!DoctorImpl.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		final DoctorImpl temp = (DoctorImpl) obj;

		if(this.doctorID == temp.doctorID && this.name.equals(temp.name) && this.medicalSpecialty.equals(temp.medicalSpecialty) && 
		   data.equals(temp.data)) {
			return true;
		}
		
		return false;
	}
	
	public boolean remove() {
		if(doctorID > 0) {
			doctorID *= -1;
			return true;
		}
		else return false;
	}
	
	public boolean unremove() {
		if(doctorID < 0) {
			doctorID *= -1;
			return true;
		}
		else return false;
	}
	
	public boolean isRemoved() {
		if(doctorID < 0)
			return true;
		else return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (name != null ? name.hashCode() : 0);
		hash = 53 * hash + (data != null ? data.hashCode() : 0);
		hash = 53 * hash + (medicalSpecialty != null ? medicalSpecialty.hashCode() : 0);
		hash = 53 * hash + doctorID;
		return hash;
	}
}
