package org.eurocris.cerif.tools;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eurocris.cerif.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TOAD2CERIF {
	public static final String CERIF_ENTITIES_UUID = "6e0d9af0-1cd6-11e1-8bc2-0800200c9a66";
	public static final String CERIF_ENTITY_TYPES_UUID = "348ce6ee-43ef-4b71-aa77-a11ff988cae4";
	public static final String CERIF_ATTRIBUTES_UUID = "318118e8-d323-4e3b-9882-a17c635e9c58";
	public static final String CERIF_RELATIONSHIPS_UUID = "fd0791d1-570b-4b7a-825f-99679a3a29cf";
	public static final String CERIF_DATAMODEL_FACTS_UUID = "2a29befc-305f-405a-b808-9ed0dc6c61ff";
	
	public static final String CERIF_DMF_APPLICABLE_UUID = "f22733fc-40c8-4a28-9071-6b49bd921621";
	public static final String CERIF_DMF_HAS_ATTRIBUTE_UUID = "836509cb-9d07-4c93-9db1-1097edc89115";
	
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static Transformer transformer;
	
	static {
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		catch (Exception ex){
			System.err.println("Error initializing the XML transformer");
		}
	}
	
	
	public static void main(String argv[]) {

		try {
			CommandLineParser parser = new PosixParser();
	    	Options options = new Options();
	    	
	    	options.addOption("f", "file", true, "full path to the TOAD file");
	    	options.addOption("o", "output", true, "full path to the folder where place the generated XMLs");
	    	options.addOption("h", "help", true, "this help message");
	    	
	    	CommandLine line = parser.parse(options, argv);
	    	
	    	if (line.hasOption("h") || !(line.hasOption("f") && line.hasOption("o")))
	    	{
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp( "toad2cerif", options );
	    		// print out the help
	    		System.exit(line.hasOption("h")?0:1);
	    	}
	    	
	    	File outputFolder = new File(line.getOptionValue('o'));
	    	if (outputFolder.exists() && !outputFolder.isDirectory())
	    	{
	    		System.out.println(line.getOptionObject('o') + "is not a folder");
	    		// print out the help
	    		System.exit(1);
	    	}
	    	
	    	if (!outputFolder.exists()) {
	    		outputFolder.mkdirs();
	    	}
			File fXmlFile = new File(line.getOptionValue('f'));
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();

			Element modelTitleUN = XMLUtils.getSingleElement(root, "ModelTitle");
			Element subModelTitleUN = XMLUtils.getSingleElement(modelTitleUN, "ModelTitleUN");
			String modifiedDate = XMLUtils.getElementValue(subModelTitleUN, "ModifiedDate");
			
			Element categories = XMLUtils.getSingleElement(root, "Categories");
			
			Document entityTypesXML = dBuilder.newDocument();
			Element cerif = createCERIFDocumentElement(fXmlFile, modifiedDate, entityTypesXML);

			Element cfClassSchema = createScheme(entityTypesXML, cerif, CERIF_ENTITY_TYPES_UUID,
					"CERIF Entity Types", "This scheme contains the available classification for the CERIF Entities");

			// I need to create the Entity Types Schema 348ce6ee-43ef-4b71-aa77-a11ff988cae4
			// and link the Classification representing the Entity to its type
			List<Element> categoriesList = XMLUtils.getElementList(categories, "CategoryUN");
			
			for (Element category : categoriesList) {
				Element cfClass = createSubElement(entityTypesXML, cfClassSchema, "cfClass");
				String cfClassId = cleanTOADUUID(XMLUtils.getElementValue(category, "Id"));
				createSubElement(entityTypesXML, cfClass, "cfClassId", cfClassId);	
				addLangTrans(createSubElement(entityTypesXML, cfClass, "cfTerm", XMLUtils.getElementValue(category, "Name")));	
				addLangTrans(createSubElement(entityTypesXML, cfClass, "cfTermSrc", "CERIF ER-M"));

				// System.out.println(XMLUtils.getElementValue(category, "Description"));
					
				Element objects = XMLUtils.getSingleElement(category, "Objects");
				for (String id : XMLUtils.getElementValueList(objects, "Id")) {
					String cfClassId2 = cleanTOADUUID(id);
					createClassClass(entityTypesXML, cfClass, cfClassId2, CERIF_ENTITIES_UUID, CERIF_DMF_APPLICABLE_UUID, CERIF_DATAMODEL_FACTS_UUID);
				}
			}
			
			// write the content into xml files
			writeToFile(outputFolder, "CERIF_Entity_Types--" + CERIF_ENTITY_TYPES_UUID+ ".xml", 
						entityTypesXML);
			
			// CERIF Attributes 318118e8-d323-4e3b-9882-a17c635e9c58
			
			Document attributesXML = dBuilder.newDocument();
			Element cerifAttr = createCERIFDocumentElement(fXmlFile, modifiedDate, attributesXML);

			Element cfClassSchemaAttr = createScheme(attributesXML, cerifAttr, CERIF_ATTRIBUTES_UUID,
					"CERIF Attributes", "This scheme contains all the available attributes of the CERIF Entities");

			Document entitiesXML = dBuilder.newDocument();
			Element cerifEntities = createCERIFDocumentElement(fXmlFile, modifiedDate, entitiesXML);

			Element cfClassSchemaEntities = createScheme(entitiesXML, cerifEntities, CERIF_ENTITIES_UUID,
					"CERIF Entities", "This scheme contains defined CERIF concepts such as person, organisation, research infrastructure (being not only a 1:1 representation of the CERIF entities), but even more, e.g. research infrastructure subsumes facilty, equipment and service and output subsumes publication, patent, and product in CERIF.");

			
			Element entities = XMLUtils.getSingleElement(root, "Entities");
			List<Element> entitiesList = XMLUtils.getElementList(entities, "PEREntityUN");

			for (Element entity : entitiesList) {
				String entityUUID = cleanTOADUUID(XMLUtils.getElementValue(entity, "Id"));
				String entityName = XMLUtils.getElementValue(entity, "Name");
				String entityLongName = XMLUtils.getElementValue(entity, "Caption");
				String entityNotes = XMLUtils.getElementValue(entity, "Notes");
				String entityComments = XMLUtils.getElementValue(entity, "Comments");
				
				Element cfEntityClass = createClass(entitiesXML, cfClassSchemaEntities, entityUUID, entityLongName, entityNotes, entityComments);
				
				Element attributesNode = XMLUtils.getSingleElement(entity, "Attributes");
				List<Element> attributes = XMLUtils.getElementList(attributesNode, "PERAttributeUN");
				for (Element attr : attributes) {
					
					String cfClassId2 = cleanTOADUUID(XMLUtils.getElementValue(attr, "Id"));
					String cfTerm = entityName + "." + XMLUtils.getElementValue(attr, "Name");
					
					createClass(attributesXML, cfClassSchemaAttr, cfClassId2, cfTerm);
					createClassClass(entitiesXML, cfEntityClass, cfClassId2, CERIF_ATTRIBUTES_UUID, CERIF_DMF_HAS_ATTRIBUTE_UUID, CERIF_DATAMODEL_FACTS_UUID);
				}
			}
			
			// write the content into xml files
			writeToFile(outputFolder, "CERIF_Entities--" + CERIF_ENTITIES_UUID + ".xml", entitiesXML);
			writeToFile(outputFolder, "CERIF_Attributes--" + CERIF_ATTRIBUTES_UUID + ".xml", attributesXML);
			
			// CERIF Relationships fd0791d1-570b-4b7a-825f-99679a3a29cf
			// CERIF data model facts 2a29befc-305f-405a-b808-9ed0dc6c61ff
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createClassClass(Document entityTypesXML, Element cfClass, String cfClassId2, String cfClassSchemeId2, String classId,
			String classSchemeId) {
		Element cfClassClass = createSubElement(entityTypesXML, cfClass, 
				"cfClass_Class");
		
//					createSubElement(entityTypesXML, cfClassClass, 
//							"cfClassId1", cfClassId);
//					createSubElement(entityTypesXML, cfClassClass, 
//							"cfClassSchemeId1", CERIF_ENTITY_TYPES_UUID);
		
		
		createSubElement(entityTypesXML, cfClassClass, 
				"cfClassId2", cfClassId2);
		createSubElement(entityTypesXML, cfClassClass, 
				"cfClassSchemeId2", cfClassSchemeId2);
		
		createSubElement(entityTypesXML, cfClassClass, 
				"cfClassId", classId);
		createSubElement(entityTypesXML, cfClassClass, 
				"cfClassSchemeId", classSchemeId);
	}

	private static Element createClass(Document attributesXML, Element cfClassSchemaAttr, String cfClassId,
			String cfTerm) {
		return createClass(attributesXML, cfClassSchemaAttr, cfClassId,
				cfTerm, null, null);
	}
	
	private static Element createClass(Document attributesXML, Element cfClassSchemaAttr, String cfClassId,
			String cfTerm, String cfDesc, String cfDef) {
		Element cfClass = createSubElement(attributesXML, cfClassSchemaAttr, "cfClass");
		createSubElement(attributesXML, cfClass, "cfClassId", cfClassId);	
		addLangTrans(createSubElement(attributesXML, cfClass, "cfTerm", cfTerm));	
		addLangTrans(createSubElement(attributesXML, cfClass, "cfTermSrc", "CERIF ER-M"));
		
		if (cfDesc != null) {
			addLangTrans(createSubElement(attributesXML, cfClass, "cfDescr", cfDesc));
			addLangTrans(createSubElement(attributesXML, cfClass, "cfDescrSrc", "CERIF Task Group"));
		}
		if (cfDef != null) {
			addLangTrans(createSubElement(attributesXML, cfClass, "cfDef", cfDef));
			addLangTrans(createSubElement(attributesXML, cfClass, "cfDefSrc", "CERIF Task Group"));
		}
		return cfClass;
	}

	private static Element createScheme(Document doc, Element cerifElement,
			String uuid, String name, String description) {
		Element cfClassSchemaAttr = createSubElement(doc, cerifElement, "cfClassScheme");
		createSubElement(doc, cfClassSchemaAttr, "cfClassSchemeId", uuid);
		addLangTrans(createSubElement(doc, cfClassSchemaAttr, "cfName", name));
		addLangTrans(createSubElement(doc, cfClassSchemaAttr, "cfDescr", description));
		return cfClassSchemaAttr;
	}

	private static void writeToFile(File outputFolder, String filename, Document entityTypesXML) throws TransformerException {
		DOMSource source = new DOMSource(entityTypesXML);
		StreamResult result = new StreamResult(
				new File(outputFolder, filename));
		transformer.transform(source, result);
	}

	private static String cleanTOADUUID(String id) {

		return id.replaceAll("[\\{\\}]", "").toLowerCase();
	}

	private static Element createCERIFDocumentElement(File fXmlFile, String modifiedDate, Document entityTypesXML) {
		Element rEntityTypesXML = entityTypesXML.createElement("CERIF");
		rEntityTypesXML.setAttribute("xmlns", "urn:xmlns:org:eurocris:cerif-1.6.1-3");
		rEntityTypesXML.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rEntityTypesXML.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "urn:xmlns:org:eurocris:cerif-1.6.1-3 http://www.eurocris.org/Uploads/Web%20pages/CERIF-1.6.1/CERIF_1.6.1_3.xsd");
		rEntityTypesXML.setAttribute("date", modifiedDate.substring(0, 10));
		rEntityTypesXML.setAttribute("sourceDatabase", fXmlFile.getName());

		entityTypesXML.appendChild(rEntityTypesXML);
		return rEntityTypesXML;
	}

	private static void addLangTrans(Element createSubElement) {
		createSubElement.setAttribute("cfLangCode", "en");
		createSubElement.setAttribute("cfTrans", "o");
	}

	private static Element createSubElement(Document doc, Element rEntityTypesXML, String tagName) {
		return createSubElement(doc, rEntityTypesXML, tagName, null);
	}
	
	private static Element createSubElement(Document doc, Element rEntityTypesXML, String tagName, String textContent) {
		Element sub = doc.createElement(tagName);
		if (textContent != null) {
			sub.setTextContent(textContent);
		}
		rEntityTypesXML.appendChild(sub);
		return sub;
	}

}