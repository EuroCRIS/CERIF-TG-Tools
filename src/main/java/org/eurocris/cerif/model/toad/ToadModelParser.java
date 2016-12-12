package org.eurocris.cerif.model.toad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eurocris.cerif.model.Attribute;
import org.eurocris.cerif.model.CERIFEntityType;
import org.eurocris.cerif.model.Entity;
import org.eurocris.cerif.model.ForeignKey;
import org.eurocris.cerif.model.Model;
import org.eurocris.cerif.model.Relationship;
import org.eurocris.cerif.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ToadModelParser {

	private static final Logger LOG = Logger.getLogger( ToadModelParser.class.getName() );
	
	public ToadModelParser() {
	}

	public Model readInModel( final File fXmlFile ) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(fXmlFile);
		// optional, but recommended: read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		final Element modelRootEl = doc.getDocumentElement();
		final Model model = readInModel( modelRootEl );
		return model;
	}

	public Model readInModel( Element modelRootEl ) {
		final Element modelTitleUN = XMLUtils.getSingleElement(modelRootEl, "ModelTitle");
		final Element subModelTitleUN = XMLUtils.getSingleElement(modelTitleUN, "ModelTitleUN");
		final String modifiedDate = XMLUtils.getElementValue(subModelTitleUN, "ModifiedDate");

		final Model model = new Model( modifiedDate, modelTitleUN.getTextContent() );

		final Element categories = XMLUtils.getSingleElement(modelRootEl, "Categories");
		final List<Element> categoriesList = XMLUtils.getElementList(categories, "CategoryUN");
		final Map<UUID, CERIFEntityType> entityTypeByEntityUUIDMap = new HashMap<>();
		for ( final Element category : categoriesList ) {
			final UUID entityTypeUUID = extractTOADUUID(XMLUtils.getElementValue(category, "Id"));
			final String entityTypeName = XMLUtils.getElementValue(category, "Name");
			model.addCategory( entityTypeUUID, entityTypeName );			
			for (CERIFEntityType entityType : CERIFEntityType.values() ) {
				if ( entityType.getUuid().equals( entityTypeUUID ) ) {
					Element objects = XMLUtils.getSingleElement(category, "Objects");
					for (String id : XMLUtils.getElementValueList(objects, "Id")) {
						UUID entityCfClassId = extractTOADUUID(id);
						entityTypeByEntityUUIDMap.put( entityCfClassId, entityType );
					}
				}
			}
		}
		
		// we read in the model
		final Element entities = XMLUtils.getSingleElement(modelRootEl, "Entities");
		final List<Element> entitiesList = XMLUtils.getElementList(entities, "PEREntityUN");
		LOG.info( "Found " + entitiesList.size() + " entities" );
	
		Map<UUID, List<UUID>> keyConstraintAttributeUUIDsByConstraintUUIDMap = new HashMap<>();
		for ( final Element entity : entitiesList ) {
			final Element keysNode = XMLUtils.getSingleElement(entity, "Keys");
			final List<Element> constraints = XMLUtils.getElementList(keysNode, "PERKeyConstraintUN");
	
			for (Element constraint : constraints) {
				final UUID constraintId = extractTOADUUID( XMLUtils.getElementValue(constraint, "Id") );
				final Element keyItems = XMLUtils.getSingleElement(constraint, "KeyItems");
				final List<Element> keyConstraints = XMLUtils.getElementList(keyItems, "PERKeyConstraintItemUN");
				final List<UUID> attributes = new ArrayList<>();
				keyConstraintAttributeUUIDsByConstraintUUIDMap.put(constraintId, attributes);
				for ( final Element keyConstraint : keyConstraints ) {
					final UUID attributeUUID = extractTOADUUID(XMLUtils.getElementValueList(keyConstraint, "Attribute", "Id").get(0));
					attributes.add(attributeUUID);
				}
			}
		}
		
		for ( final Element entityEl : entitiesList ) {
			final UUID entityUUID = extractTOADUUID(XMLUtils.getElementValue(entityEl, "Id"));
			final String entityName = XMLUtils.getElementValue(entityEl, "Name");
			final String entityLongName = XMLUtils.getElementValue(entityEl, "Caption");
			final String readableEntityLongName = makeReadable( entityLongName );
			final String entityNotes = XMLUtils.getElementValue(entityEl, "Notes");
			final String entityComments = XMLUtils.getElementValue(entityEl, "Comments");
			final CERIFEntityType entityType = entityTypeByEntityUUIDMap.get( entityUUID );
			final String cfTerm = extractInfo( "term", entityNotes, readableEntityLongName );
			final String pkConstraintId = extractTOADUUID( XMLUtils.getElementValueList(entityEl, "PK", "Id").get(0) ).toString();
	
			final Entity entity = new Entity( entityUUID, entityName, readableEntityLongName, entityType, cfTerm, entityNotes, entityComments, pkConstraintId );
			model.add( entity );
			
			final List<UUID> pks = keyConstraintAttributeUUIDsByConstraintUUIDMap.get(UUID.fromString( pkConstraintId ));
			final Element attributesNode = XMLUtils.getSingleElement(entityEl, "Attributes");
			final List<Element> attributes = XMLUtils.getElementList(attributesNode, "PERAttributeUN");
			for ( final Element attr : attributes ) {
				final UUID attrUUID = extractTOADUUID(XMLUtils.getElementValue(attr, "Id"));
				final String attrBareName = XMLUtils.getElementValue(attr, "Name");
				final String attrComposedName = entityName + "." + attrBareName;
				final String attrLongName = XMLUtils.getElementValue( attr, "Caption" );
				final String readableAttrLongName = makeReadable( attrLongName );
				final String attrNotes = XMLUtils.getElementValue( attr, "Notes" );
				final String attrComments = XMLUtils.getElementValue( attr, "Comments" );
				
				final Attribute attribute = new Attribute( entity, attrUUID, attrComposedName, readableAttrLongName, attrNotes, attrComments );
				model.add( attribute );
				if (pks.contains(attrUUID)) {
					entity.getPrimaryKey().getAttributes().add( attribute );
				}
			}
		}
		
		final Element relations = XMLUtils.getSingleElement(modelRootEl, "Relations");
		final List<Element> relationsList = XMLUtils.getElementList(relations, "PERRelationUN");
		for ( final Element relation : relationsList ) {
			final UUID relationshipUUID = extractTOADUUID(XMLUtils.getElementValue(relation, "Id"));
			// the official name for the relationship connector is stored in the caption element in the TOAD file
			final String name = XMLUtils.getElementValue(relation, "Caption");
			final String notes = XMLUtils.getElementValue(relation, "Notes");
			final String comments = XMLUtils.getElementValue(relation, "Comments");
			final Element o1El = XMLUtils.getSingleElement(relation, "O1");
			final Element o2El = XMLUtils.getSingleElement(relation, "O2");
			final UUID parentEntityUUID = extractTOADUUID(XMLUtils.getElementValue(o1El, "Id"));
			final UUID childEntityUUID = extractTOADUUID(XMLUtils.getElementValue(o2El, "Id"));
			final Entity parentEntity = model.getEntityBy( parentEntityUUID );
			final Entity childEntity = model.getEntityBy( childEntityUUID );
			final ForeignKey fk = new ForeignKey( childEntity, name );
	
			final List<Attribute> fkAttrList = fk.getAttributes();
			final Element foreignKeys = XMLUtils.getSingleElement(relation, "ForeignKeys");
			final List<Element> fkListEl = XMLUtils.getElementList(foreignKeys, "PERForeignKeyUN");
			for ( final Element fkEl : fkListEl ) {
				final UUID childAttrUUID = extractTOADUUID(XMLUtils.getElementValueList(fkEl, "AttrChild", "Id").get(0));
				final Attribute childAttr = model.getAttributeBy( childAttrUUID );
				fkAttrList.add( childAttr );
			}
			final Relationship relationship = new Relationship( relationshipUUID, name, parentEntity.getPrimaryKey(), fk, notes, comments );
			model.add( relationship );
		}
		
		return model;
	}

	private static String makeReadable( final String name ) {
		return name.substring(2).replaceAll("([A-Z])", " $0").trim();
	}

	public static String cleanText(String cfDesc) {
		if (cfDesc != null) {
			return cfDesc.replaceAll("\\{@[a-z]* .*?\\}","");
		}
		return null;
	}

	public static String extractInfo( final String param, final String cfDesc, final String defValue ) {
		if ( cfDesc != null ) {
			Pattern p = Pattern
					.compile( "\\{@" + param + " (.*?)\\}(?:\\{@[a-z]* .*?\\})*.*|(?:\\{@[a-z]* .*?\\})*?\\{@" + param + " (.*?)\\}.*" );
			Matcher m = p.matcher( cfDesc );
			if ( m.matches() ) {
				return ( m.group(1) != null ) ? m.group(1) : m.group(2);
			}
		}
		return defValue;
	}

	private static UUID extractTOADUUID(String id) {
		return UUID.fromString( id.replaceAll("[\\{\\}]", "").toLowerCase() );
	}

}
