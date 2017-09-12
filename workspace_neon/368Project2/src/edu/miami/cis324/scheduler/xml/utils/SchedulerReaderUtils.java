package edu.miami.cis324.scheduler.xml.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.miami.cis324.scheduler.xml.data.Doctor;
import edu.miami.cis324.scheduler.xml.data.DoctorImpl;
import edu.miami.cis324.scheduler.xml.data.MedicalSpecialty;
import edu.miami.cis324.scheduler.xml.data.Name;
import edu.miami.cis324.scheduler.xml.data.Patient;
import edu.miami.cis324.scheduler.xml.data.PatientImpl;
import edu.miami.cis324.scheduler.xml.data.PersonalData;
import edu.miami.cis324.scheduler.xml.data.Scheduler;
import edu.miami.cis324.scheduler.xml.data.Visit;
import edu.miami.cis324.scheduler.xml.data.VisitImpl;
import edu.miami.cis324.xml.utils.XMLReaderUtils;

public final class SchedulerReaderUtils extends SchedulerReadWriteUtils {
	private final static String DOB_FORMAT = "yyyy-MM-dd";
	
	public static Name readName(XMLEventReader eventReader) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a name but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(NAME)) {
			throw new IllegalStateException("Attempting to read a name at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		// now we read the name
		// first, read the attributes 

		// now we read the next events until we find the end event
		Name name = null;
		String firstName = null;
		String lastName = null;
		boolean finished = false;
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(FIRST_NAME)) {
					firstName = XMLReaderUtils.readCharacters(eventReader, FIRST_NAME);
				}
				else if (startElement.getName().getLocalPart().equals(LAST_NAME)) {
					lastName = XMLReaderUtils.readCharacters(eventReader, LAST_NAME);
				}
				else {
					System.err.println("Unrecognized name element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(NAME)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					name = new Name(firstName, lastName);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		return name;
	}

	public static PersonalData readData(XMLEventReader eventReader) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read personal data but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(DATA)) {
			throw new IllegalStateException("Attempting to read personal data at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		// now we read the data
		// now we read the next events until we find the end event
		PersonalData data = null;
		String ssn = null;
		Calendar birthDate = null;
		boolean finished = false;
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(SSN)) {
					ssn = XMLReaderUtils.readCharacters(eventReader, SSN);
				}
				else if (startElement.getName().getLocalPart().equals(DOB)) {
					birthDate = XMLReaderUtils.readDate(eventReader, DOB, DOB_FORMAT);
				}
				else {
					System.err.println("Unrecognized data element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(DATA)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					data = new PersonalData(birthDate.getTime(), ssn);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		return data;
	}
	
	public static Patient readPatient(XMLEventReader eventReader) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a patient but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(PATIENT)) {
			throw new IllegalStateException("Attempting to read a patient at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		// now we read the student
		// first, read the attributes 
		int patientID = -1;
		@SuppressWarnings("unchecked") // getAttributes() guarantees type
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(PATIENT_ID)) {
				patientID = Integer.valueOf(attribute.getValue());
			}
			else {
				System.err.println("Found unknown patient attribute, ignoring; found: " + attribute.getName());
			}
		}
		// now we read the next events until we find the end event
		Patient patient = null;
		Name name = null;
		PersonalData data = null;
		boolean finished = false;
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(NAME)) {
					name = readName(eventReader);
				}
				else if (startElement.getName().getLocalPart().equals(DATA)) {
					data = readData(eventReader);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(PATIENT)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					patient = new PatientImpl(name, data, patientID);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		return patient;
	}

	public static Doctor readDoctor(XMLEventReader eventReader) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a doctor but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(DOCTOR)) {
			throw new IllegalStateException("Attempting to read a doctor at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		// now we read the student
		// first, read the attributes 
		int doctorID = -1;
		@SuppressWarnings("unchecked") // getAttributes() guarantees type
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(DOCTOR_ID)) {
				doctorID = Integer.valueOf(attribute.getValue());
			}
			else {
				System.err.println("Found unknown doctor attribute, ignoring; found: " + attribute.getName());
			}
		}
		// now we read the next events until we find the end event
		Doctor doctor = null;
		Name name = null;
		PersonalData data = null;
		MedicalSpecialty ms = null;
		boolean finished = false;
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(NAME)) {
					name = readName(eventReader);
				}
				else if (startElement.getName().getLocalPart().equals(DATA)) {
					data = readData(eventReader);
				}
				else if (startElement.getName().getLocalPart().equals(MEDICAL_SPECIALTY)) {
					String msStr = XMLReaderUtils.readCharacters(eventReader, MEDICAL_SPECIALTY);
					ms = MedicalSpecialty.valueOf(msStr); // needs error checking to ensure that the state string exists
				}
				else {
					System.err.println("Unrecognized doctor element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(DOCTOR)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					doctor = new DoctorImpl(name, data, ms, doctorID);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		return doctor;
	}
	
	public static Visit<Integer, Integer> readVisit(XMLEventReader eventReader) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a visit but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(VISIT)) {
			throw new IllegalStateException("Attempting to read a visit at the wrong start element: found " + firstEvent.asStartElement().getName());
		}

		// now we read the next events until we find the end event
		Visit<Integer, Integer> visit = null;
		int patientID = -1;
		int doctorID = -1;
		Date visitDate = null;
		
		@SuppressWarnings("unchecked") 
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(DOCTOR_ID)) {
				doctorID = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(PATIENT_ID)) {
				patientID = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(VISIT_DATE)) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				try {
					visitDate = format.parse(attribute.getValue());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.err.println("Found unknown visit attribute, ignoring; found: " + attribute.getName());
			}
		}
		
		boolean finished = false;
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				System.err.println("Unrecognized element, ignoring: " + startElement.getName());
				event = eventReader.nextEvent(); // skip this event and read the next
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(VISIT)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					visit = new VisitImpl<Integer, Integer>(patientID, doctorID, visitDate);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		return visit;
	}
	
	public static Scheduler readScheduler(String xmlFile) throws XMLStreamException, IOException {
		Scheduler scheduler = new Scheduler();
		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		Path xmlFilePath = Paths.get(xmlFile);
		Reader in = Files.newBufferedReader(xmlFilePath, StandardCharsets.UTF_8);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		// Read the XML document
		while (eventReader.hasNext()) {
			// peek the next event
			// use peek so that we can actually read and check the next start element as it happens
			XMLEvent event = eventReader.peek(); 
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				String local = startElement.getName().getLocalPart();
				if (local == ROOT) {
					// just read the next event, which should be an student
					event = eventReader.nextEvent(); // skip this event and read the next
				}
				// if we are at the top element for an student
				else if (local == DOCTOR) {
					Doctor d = readDoctor(eventReader);
					scheduler.addDoctor(d);
				}
				
				else if (local == PATIENT) {
					Patient p = readPatient(eventReader);
					scheduler.addPatient(p);
				}
				
				else if (local == VISIT) {
					Visit<Integer, Integer> v = readVisit(eventReader);
					scheduler.addVisit(v);
				}
				
				else {
					System.err.println("Unrecognized root element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			else {
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		eventReader.close();
		return scheduler;
	}


}
