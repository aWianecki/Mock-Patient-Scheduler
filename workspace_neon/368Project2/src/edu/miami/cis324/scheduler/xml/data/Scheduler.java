package edu.miami.cis324.scheduler.xml.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler implements Serializable{
	Map<Integer, Patient> patients;
	Map<Integer, Doctor> doctors;
	List<Visit<Integer, Integer>> visits;
	
	public Scheduler() {
		patients = new HashMap<Integer, Patient>();
		doctors = new HashMap<Integer, Doctor>();
		visits = new ArrayList<Visit<Integer, Integer>>();
	}
	
	public Map<Integer, Patient> getPatients() {
		return patients;
	}
	
	public Map<Integer, Doctor> getDoctors() {
		return doctors;
	}
	
	public List<Visit<Integer, Integer>> getVisits() {
		return visits;
	}
	
	public void addPatient(Patient p) {
		patients.put(p.getPatientID(), p);
	}
	
	public void addDoctor(Doctor d) {
		doctors.put(d.getDoctorID(), d);
	}
	
	public void addVisit(Visit<Integer, Integer> v) {
		visits.add(v);
	}
	
	public void print() {
		System.out.println("Patients:");
		for(Patient p : patients.values()) {
			System.out.println(p.getFirstName() + " " + p.getLastName());
		}
		System.out.println("\nDoctors:");
		for(Doctor d : doctors.values()) {
			System.out.println(d.getFirstName() + " " + d.getLastName());
		}
		System.out.println("\nVisits:");
		for(int i = 0; i < visits.size(); i++) {
			Visit<Integer, Integer> v = visits.get(i);
			System.out.println(v.getHost() + " " + v.getVisitor() + " ");
		}
	}
}
