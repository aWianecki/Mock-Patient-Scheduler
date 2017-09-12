package edu.miami.cis324.scheduler.xml.data;

import java.io.Serializable;

public class Name implements Serializable{
	private String firstName;
	private String lastName;
	
	public Name() {
		firstName = "";
		lastName = "";
	}
	
	public Name(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Name(String fullName) {
		String[] temp = fullName.split("\\s");
		if(temp.length != 2) {
			firstName = "";
			lastName = "";
		}
		else {
			firstName = temp[0];
			lastName = temp[1];
		}
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getFirstLast() {
		return firstName + " " + lastName;
	}
	
	public String getLastFirst() {
		return lastName + ", " + firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public int compareTo(Name anotherName) {
		int compare = this.lastName.compareTo(anotherName.getLastName());
		if (compare != 0) {
			return compare;
		}
		
		compare = this.firstName.compareTo(anotherName.getFirstName());
		if (compare != 0) {
			return compare;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
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

		if(this.firstName.equals(temp.getFirstName()) && this.lastName.equals(temp.getLastName())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (firstName != null ? firstName.hashCode() : 0);
		hash = 53 * hash + (lastName != null ? lastName.hashCode() : 0);
		return hash;
	}
}
