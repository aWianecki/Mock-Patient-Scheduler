package edu.miami.cis324.scheduler.xml.utils;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.Name;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.PersonalData;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.data.Visit;
import edu.miami.cis324.xml.utils.XMLWriterUtils;

public final class SchedulerWriterUtils extends SchedulerReadWriteUtils {

	private final static String NAMESPACE = "http://www.miami.edu/cis324/xml/scheduling";
	private final static String SCHEMA_INSTANCE_PREFIX = "xsi";
	private final static String SCHEMA_INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String SCHEMA_LOCATION_ATTRNAME = "schemaLocation";
	private final static String SCHEMA_FILE_NAME = "Scheduling.xsd";
	
	public static void writeName(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Name name, int level) throws XMLStreamException {
		// first, write as many tabs as levels needed
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
		// start element
		eventWriter.add(eventFactory.createStartElement("", "", NAME));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
		// first name
		XMLWriterUtils.writeNode(eventFactory, eventWriter, FIRST_NAME, name.getFirstName(), level+1);
		// last name
		XMLWriterUtils.writeNode(eventFactory, eventWriter, LAST_NAME, name.getLastName(), level+1);
		// end element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level)); // also indent it
		eventWriter.add(eventFactory.createEndElement("", "", NAME));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	}
	
	public static void writePersonalData(XMLEventFactory eventFactory, XMLEventWriter eventWriter, PersonalData data, int level) throws XMLStreamException {
		// first, write as many tabs as levels needed
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
		// start element
		eventWriter.add(eventFactory.createStartElement("", "", DATA));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
		// birthdate
		XMLWriterUtils.writeNode(eventFactory, eventWriter, DOB, data.getStrBirthdate(), level+1);
		// SSN
		XMLWriterUtils.writeNode(eventFactory, eventWriter, SSN, data.getSsn(), level+1);
		// end element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level)); // also indent it
		eventWriter.add(eventFactory.createEndElement("", "", DATA));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	}
	
	public static void writePatient(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Patient p, int level) throws XMLStreamException {
		// writes a single patient through to the XML event writer
		// create the patient start element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    StartElement patientStart = eventFactory.createStartElement("", "", PATIENT);
	    eventWriter.add(patientStart);
	    // create the id attribute
	    // note the use of Integer.toString to get a string representation
	    Attribute patientID = eventFactory.createAttribute(PATIENT_ID, Integer.toString(p.getPatientID()));
	    eventWriter.add(patientID);
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	    // now create the nested elements
		writeName(eventFactory, eventWriter, p.getName(), level + 1);
		writePersonalData(eventFactory, eventWriter, p.getData(), level + 1);
	    // create the patient end element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    EndElement patientEnd = eventFactory.createEndElement("", "", PATIENT);
	    eventWriter.add(patientEnd);
	}
	
	public static void writeDoctor(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Doctor d, int level) throws XMLStreamException {
		// writes a single doctor through to the XML event writer
		// create the doctor start element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    StartElement doctorStart = eventFactory.createStartElement("", "", DOCTOR);
	    eventWriter.add(doctorStart);
	    // create the id attribute
	    // note the use of Integer.toString to get a string representation
	    Attribute doctorID = eventFactory.createAttribute(DOCTOR_ID, Integer.toString(d.getDoctorID()));
	    eventWriter.add(doctorID);
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	    // now create the nested elements
		writeName(eventFactory, eventWriter, d.getName(), level + 1);
	    writePersonalData(eventFactory, eventWriter, d.getData(), level + 1);
	    XMLWriterUtils.writeNode(eventFactory, eventWriter, MEDICAL_SPECIALTY, d.getMedicalSpecialty().toString(), level + 1);
	    // create the doctor end element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    EndElement doctorEnd = eventFactory.createEndElement("", "", DOCTOR);
	    eventWriter.add(doctorEnd);
	}
	
	public static void writeVisit(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Visit<Integer, Integer> v, int level) throws XMLStreamException {
		// writes a single visit through to the XML event writer
		// create the visit start element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    StartElement visitStart = eventFactory.createStartElement("", "", VISIT);
	    eventWriter.add(visitStart);
	    Attribute patientID = eventFactory.createAttribute(PATIENT_ID, Integer.toString(v.getVisitor()));
	    eventWriter.add(patientID);
	    Attribute doctorID = eventFactory.createAttribute(DOCTOR_ID, Integer.toString(v.getHost()));
	    eventWriter.add(doctorID);
	    
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ignore time zones for simplicity
		String dateStr = df.format(v.getVisitDate());
	    
	    Attribute visitDate = eventFactory.createAttribute(VISIT_DATE, dateStr);
	    eventWriter.add(visitDate);
	    // create the visit end element
		eventWriter.add(XMLWriterUtils.getIndentation(eventFactory, level));
	    EndElement visitEnd = eventFactory.createEndElement("", "", VISIT);
	    eventWriter.add(visitEnd);
	}

	public static void writeScheduler(String outFile, Scheduler scheduler) throws XMLStreamException, IOException {
	    // Create a XMLOutputFactory
	    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	    // Create XMLEventWriter
	    Path outputFilePath = Paths.get(outFile);
	    Writer outputFile = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
	    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputFile);
	    // Create an XMLEventFactory
	    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	    // Create and write Start Tag
	    StartDocument startDocument = eventFactory.createStartDocument("UTF-8", "1.0");
	    eventWriter.add(startDocument);
	    // put a linefeed for readability
	    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
	    // create the root element
	    StartElement root = eventFactory.createStartElement("", "", ROOT);
		eventWriter.add(root);
	    eventWriter.setDefaultNamespace(SchedulerWriterUtils.NAMESPACE); // set the default namespace for the root before adding it
		// add any other namespaces to the root
	    eventWriter.add(eventFactory.createNamespace(NAMESPACE));
	    eventWriter.add(eventFactory.createNamespace(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_NS));
	    // add the schema attributes to the root element 
	    String schemaLocationArg = NAMESPACE + " " + SCHEMA_FILE_NAME;
	    eventWriter.add(eventFactory.createAttribute(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_NS, SCHEMA_LOCATION_ATTRNAME, schemaLocationArg));
	    // put a linefeed for readability
	    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		// iterate over the list of students and create an element for each
		
	    Map<Integer, Patient> patients = scheduler.getPatients();
	    Map<Integer, Doctor> doctors = scheduler.getDoctors();

	    for (Map.Entry<Integer, Patient> entry : patients.entrySet())
	    {
	        Patient p = entry.getValue();
	        writePatient(eventFactory, eventWriter, p, 1); // write the patient with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
	    }
	    
	    for (Map.Entry<Integer, Doctor> entry : doctors.entrySet())
	    {
	        Doctor d = entry.getValue();
	        writeDoctor(eventFactory, eventWriter, d, 1); // write the patient with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
	    }
		
		for (Visit<Integer, Integer> v : scheduler.getVisits()) {
			writeVisit(eventFactory, eventWriter, v, 1); // write the student with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		}
		
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
	}



}
