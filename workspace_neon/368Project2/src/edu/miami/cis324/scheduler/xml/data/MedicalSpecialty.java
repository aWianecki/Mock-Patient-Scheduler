package edu.miami.cis324.scheduler.xml.data;

public enum MedicalSpecialty {
	GENERAL_MEDICINE, PEDIATRICS, ONCOLOGY, ERROR;
	
	public static MedicalSpecialty getFromString(String medicalSpecialty) {
		try {
			return MedicalSpecialty.valueOf(medicalSpecialty.toUpperCase());
		} catch (IllegalArgumentException iae) {
			return ERROR;
		}
	}	
}
