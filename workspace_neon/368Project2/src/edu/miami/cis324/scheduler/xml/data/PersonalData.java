package edu.miami.cis324.scheduler.xml.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalData implements Serializable {
	private Date birthdate;
	private String ssn;
	private String format = "yyyy-MM-dd";
	
	public PersonalData() {
		birthdate = null;
		ssn = "";
	}
	
	public PersonalData(Date birthdate, String ssn) {
		this.birthdate = birthdate;
		this.ssn = ssn;
	}
	
	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getStrBirthdate() {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(birthdate);
	}
	
	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	
	public int compareTo(PersonalData anotherPersonalData) {
		int compare = this.birthdate.compareTo(anotherPersonalData.getBirthdate());
		if (compare != 0) {
			return compare;
		}
		
		compare = this.ssn.compareTo(anotherPersonalData.getSsn());
		if (compare != 0) {
			return compare;
		}

		return 0;
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
		
		if(this.birthdate == temp.getBirthdate() && this.ssn.equals(temp.getSSN())) {
					return true;
				}
				
				return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (birthdate != null ? birthdate.hashCode() : 0);
		hash = 53 * hash + (ssn != null ? ssn.hashCode() : 0);
		return hash;
	}
}
