package org.eurocris.cerif.tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TOAD2CERIF {
	public static final String CERIF_ENTITIES_UUID = "6e0d9af0-1cd6-11e1-8bc2-0800200c9a66";
	public static final String CERIF_ENTITY_TYPES_UUID = "348ce6ee-43ef-4b71-aa77-a11ff988cae4";
	public static final String CERIF_ATTRIBUTES_UUID = "318118e8-d323-4e3b-9882-a17c635e9c58";
	public static final String CERIF_RELATIONSHIPS_UUID = "fd0791d1-570b-4b7a-825f-99679a3a29cf";
	
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
	    	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			File cerifDMFFile = new File( outputFolder, "CERIF_Data_Model_Facts--2a29befc-305f-405a-b808-9ed0dc6c61ff.xml" );
	    	Document cerifDMFDoc = dBuilder.parse( cerifDMFFile );
	    	Element cerifDMFSchemeEl = XMLUtils.getSingleElement( cerifDMFDoc.getDocumentElement(), "cfClassScheme" );
	    	Element cerifDMFApplicableCfClassEl = findByCfClassId( cerifDMFSchemeEl, "f22733fc-40c8-4a28-9071-6b49bd921621" );
	    	Element cerifDMFHasAttributeCfClassEl = findByCfClassId( cerifDMFSchemeEl, "836509cb-9d07-4c93-9db1-1097edc89115" );
	    	
			File fXmlFile = new File(line.getOptionValue('f'));
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			Element modelRootEl = doc.getDocumentElement();

			Element modelTitleUN = XMLUtils.getSingleElement(modelRootEl, "ModelTitle");
			Element subModelTitleUN = XMLUtils.getSingleElement(modelTitleUN, "ModelTitleUN");
			String modifiedDate = XMLUtils.getElementValue(subModelTitleUN, "ModifiedDate");
			
			Element categories = XMLUtils.getSingleElement(modelRootEl, "Categories");
			
			Document entityTypesXML = dBuilder.newDocument();
			Element entityTypesRootEl = createCERIFDocumentElement(fXmlFile, modifiedDate, entityTypesXML);
			Element entityTypesSchemeEl = createCfClassSchemeElement(entityTypesRootEl, CERIF_ENTITY_TYPES_UUID, "CERIF Entity Types",
					"This scheme contains the available classification for the CERIF Entities");

			// I need to create the Entity Types Schema 348ce6ee-43ef-4b71-aa77-a11ff988cae4
			// and link the Classification representing the Entity to its type
			List<Element> categoriesList = XMLUtils.getElementList(categories, "CategoryUN");
			
			Map<String, Element> entityTypeMap = new HashMap<>();
			
			for (Element category : categoriesList) {
				String entityTypeUUID = cleanTOADUUID(XMLUtils.getElementValue(category, "Id"));
				String entityTypeName = XMLUtils.getElementValue(category, "Name");
				Element entityTypeClassEl = createCfClassElement(entityTypesSchemeEl, entityTypeUUID, entityTypeName);

				// System.out.println(XMLUtils.getElementValue(category, "Description"));
					
				Element objects = XMLUtils.getSingleElement(category, "Objects");
				for (String id : XMLUtils.getElementValueList(objects, "Id")) {
					String entityCfClassId = cleanTOADUUID(id);
					entityTypeMap.put( entityCfClassId, entityTypeClassEl );
				}
			}
			
			// write the content into xml files
			writeToFile(outputFolder, "CERIF_Entity_Types--" + CERIF_ENTITY_TYPES_UUID+ ".xml", 
						entityTypesXML);
			
			// CERIF Attributes 318118e8-d323-4e3b-9882-a17c635e9c58
			
			Document attributesXML = dBuilder.newDocument();
			Element attributesRootEl = createCERIFDocumentElement(fXmlFile, modifiedDate, attributesXML);
			Element attributesSchemeEl = createCfClassSchemeElement(attributesRootEl, CERIF_ATTRIBUTES_UUID, "CERIF Attributes",
					"This scheme contains all the available attributes of the CERIF Entities");

			Document entitiesXML = dBuilder.newDocument();
			Element entitiesRootEl = createCERIFDocumentElement(fXmlFile, modifiedDate, entitiesXML);
			Element entitiesSchemeEl = createCfClassSchemeElement(entitiesRootEl, CERIF_ENTITIES_UUID, "CERIF Entities",
					"This scheme contains defined CERIF concepts such as person, organisation, research infrastructure (being not only a 1:1 representation of the CERIF entities), but even more, e.g. research infrastructure subsumes facilty, equipment and service and output subsumes publication, patent, and product in CERIF.");
			
			Element entities = XMLUtils.getSingleElement(modelRootEl, "Entities");
			List<Element> entitiesList = XMLUtils.getElementList(entities, "PEREntityUN");

			for (Element entity : entitiesList) {
				String entityUUID = cleanTOADUUID(XMLUtils.getElementValue(entity, "Id"));
				String entityName = XMLUtils.getElementValue(entity, "Name");
				String entityLongName = XMLUtils.getElementValue(entity, "Caption");
				String entityNotes = XMLUtils.getElementValue(entity, "Notes");
				String entityComments = XMLUtils.getElementValue(entity, "Comments");
				Element entityTypeCfClassEl = entityTypeMap.get( entityUUID );
				
				Element entityClassEl = createCfClassElement(entitiesSchemeEl, entityUUID, entityLongName, entityNotes, entityComments);
				createCfClassClass2Element(entityClassEl, entityTypeCfClassEl, cerifDMFApplicableCfClassEl);
				
				Element attributesNode = XMLUtils.getSingleElement(entity, "Attributes");
				List<Element> attributes = XMLUtils.getElementList(attributesNode, "PERAttributeUN");
				for (Element attr : attributes) {
					
					String attrUUID = cleanTOADUUID(XMLUtils.getElementValue(attr, "Id"));
					String attrName = entityName + "." + XMLUtils.getElementValue(attr, "Name");
					
					Element attrCfClassEl = createCfClassElement(attributesSchemeEl, attrUUID, attrName);
					createCfClassClass1Element(entityClassEl, attrCfClassEl, cerifDMFHasAttributeCfClassEl);
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

	private static Element findByCfClassId( Element cfClassSchemeEl, String cfClassIdSearched ) {
		final NodeList children = cfClassSchemeEl.getChildNodes();
		final int n = children.getLength();
		for ( int i = 0; i < n; ++i ) {
			final Node child = children.item( i );
			if ( child instanceof Element ) {
				final Element cfClassEl = (Element) child;
				final String cfClassId = XMLUtils.getElementValue( cfClassEl, "cfClassId" );
				if ( cfClassIdSearched.equals( cfClassId ) ) {
					return cfClassEl;
				}
			}
		}
		return null;
	}

	private static Element createCfClassClass1Element(Element parentEl, Element cfClass2El, Element roleCfClassEl) {
		if ( cfClass2El != null ) {
			Element cfClassClassEl = createSubElement(parentEl, "cfClass_Class");
			addCfClassReference( cfClassClassEl, cfClass2El, "2" );
			addCfClassReference( cfClassClassEl, roleCfClassEl, "" );
			return cfClassClassEl;
		} else {
			return null;
		}
	}

	private static Element createCfClassClass2Element(Element parentEl, Element cfClass1El, Element roleCfClassEl ) {
		if ( cfClass1El != null ) {
			Element cfClassClassEl = createSubElement(parentEl, "cfClass_Class");
			addCfClassReference( cfClassClassEl, cfClass1El, "1" );
			addCfClassReference( cfClassClassEl, roleCfClassEl, "" );
			return cfClassClassEl;
		} else {
			return null;
		}
	}

	private static void addCfClassReference(Element parentEl, Element cfClassEl, String idSuffix) {
		Document doc = parentEl.getOwnerDocument();
		parentEl.appendChild( XMLUtils.cloneElementAs( XMLUtils.getSingleElement( cfClassEl, "cfClassId" ), doc, "cfClassId" + idSuffix ) );
		Element cfClassSchemeEl = (Element) cfClassEl.getParentNode();
		parentEl.appendChild( XMLUtils.cloneElementAs( XMLUtils.getSingleElement( cfClassSchemeEl, "cfClassSchemeId" ), doc, "cfClassSchemeId" + idSuffix ) );
	}
	
	private static Element createCfClassElement(Element parentEl, String cfClassId, String cfTerm) {
		return createCfClassElement(parentEl, cfClassId, cfTerm, null, null);
	}
	
	private static Element createCfClassElement(Element parentEl, String cfClassId, String cfTerm, String cfDesc, String cfDef) {
		Element cfClassEl = createSubElement(parentEl, "cfClass");
		createSubElement(cfClassEl, "cfClassId", cfClassId, cfTerm);	
		addLangTrans(createSubElement(cfClassEl, "cfTerm", cfTerm));	
		addLangTrans(createSubElement(cfClassEl, "cfTermSrc", "CERIF ER-M"));
		
		if (cfDesc != null) {
			addLangTrans(createSubElement(cfClassEl, "cfDescr", cfDesc));
			addLangTrans(createSubElement(cfClassEl, "cfDescrSrc", "CERIF Task Group"));
		}
		if (cfDef != null) {
			addLangTrans(createSubElement(cfClassEl, "cfDef", cfDef));
			addLangTrans(createSubElement(cfClassEl, "cfDefSrc", "CERIF Task Group"));
		}
		return cfClassEl;
	}

	private static Element createCfClassSchemeElement(Element parentEl, String uuid, String name, String description ) {
		Element cfClassSchemaAttr = createSubElement(parentEl, "cfClassScheme");
		createSubElement(cfClassSchemaAttr, "cfClassSchemeId", uuid, name);
		addLangTrans(createSubElement(cfClassSchemaAttr, "cfName", name));
		addLangTrans(createSubElement(cfClassSchemaAttr, "cfDescr", description));
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

	private static Element createCERIFDocumentElement(File fXmlFile, String modifiedDate, Document doc) {
		Element cerifElement = doc.createElement("CERIF");
		cerifElement.setAttribute("xmlns", "urn:xmlns:org:eurocris:cerif-1.6.1-3");
		cerifElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		cerifElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "urn:xmlns:org:eurocris:cerif-1.6.1-3 http://www.eurocris.org/Uploads/Web%20pages/CERIF-1.6.1/CERIF_1.6.1_3.xsd");
		cerifElement.setAttribute("date", modifiedDate.substring(0, 10));
		cerifElement.setAttribute("sourceDatabase", fXmlFile.getName());
		return (Element) doc.appendChild(cerifElement);
	}

	private static void addLangTrans(Element element) {
		element.setAttribute("cfLangCode", "en");
		element.setAttribute("cfTrans", "o");
	}

	private static Element createSubElement(Element parentEl, String tagName ) {
		return createSubElement(parentEl, tagName, null);
	}
	
	private static Element createSubElement(Element parentEl, String tagName, String textContent ) {
		return createSubElement(parentEl, tagName, textContent, null);
	}
	
	private static Element createSubElement(Element parentEl, String tagName, String textContent, String comment ) {
		Document doc = parentEl.getOwnerDocument();
		Element sub = doc.createElement(tagName);
		if (textContent != null) {
			sub.setTextContent(textContent);
			if (comment != null) {
				sub.appendChild( doc.createComment( " " + comment + " " ) );
			}
		}
		return (Element) parentEl.appendChild(sub);
	}

}