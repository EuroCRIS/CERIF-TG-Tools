package org.eurocris.cerif.tools;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.extensions.ExtensionDeserializer;
import org.apache.ws.commons.schema.extensions.ExtensionRegistry;
import org.eurocris.cerif.model.Attribute;
import org.eurocris.cerif.model.CERIFEntityType;
import org.eurocris.cerif.model.Entity;
import org.eurocris.cerif.model.Model;
import org.eurocris.cerif.model.toad.ToadModelParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class ToadXsdTools {

	public static final String CFLINK_NS_URI = "http://eurocris.org/cerif/annotations#";
	
	private static final String SUBSTITUTION_GROUP_HEAD_ENDING = "__SubstitutionGroupHead";

	public static void main( final String[] args ) {
		try {

			final CommandLineParser parser = new DefaultParser();
	    	final Options options = new Options();
	    	options.addOption("m", "model", true, "full path to the TOAD file");
	    	options.addOption("s", "schema", true, "full path to the XSD file");
	    	options.addOption("h", "help", true, "this help message");
	    	
	    	final CommandLine line = parser.parse(options, args);
	    	
	    	if (line.hasOption("h") || !(line.hasOption("m") && line.hasOption("s")))
	    	{
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp( "toad2cerif", options );
	    		System.exit(line.hasOption("h")?0:1);
	    	}
	    	
	    	final File modelFile = new File(line.getOptionValue('m'));
			final ToadModelParser modelParser = new ToadModelParser();
			final Model model = modelParser.readInModel( modelFile );
			
			final SortedMap<String, Entity> basicEntitiesByName = new TreeMap<>();
			final SortedMap<String, Attribute> basicAttributesByName = new TreeMap<>();
			for ( final Entity e : model.iterableEntities() ) {
				final CERIFEntityType et = e.getEntityType();
				if ( et != null ) {
					switch ( et ) {
					case BASE_ENTITIES:
					case ADDITIONAL_ENTITIES:
					case INDICATORS_AND_MEASUREMENTS:
					case INFRASTRUCTURE_ENTITIES:
					case RESULT_ENTITIES:
					case SECOND_ORDER_ENTITIES:
						basicEntitiesByName.put( e.getPhysicalName(), e );
					case MULTILINGUAL:
						final List<Attribute> pkAttributes = e.getPrimaryKey().getAttributes();
						streamOfNonDeprecatedAttributes( e ).filter( (a) -> !pkAttributes.contains( a ) ).forEach( (a) -> basicAttributesByName.put( a.getPhysicalName(), a ) ); 
						break;
					default:
						break;
					}
				} else {
					System.err.println( "Entity '" + e.getPhysicalName() + "' doesn't have a type" );
				}
			}

			final File schemaFile = new File(line.getOptionValue('s'));
			final XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			schemaCol.setExtReg( new MyExtensionRegistry( model ) );
			final XmlSchema schema = schemaCol.read(new StreamSource(schemaFile));
			final Map<QName, XmlSchemaElement> elements = schema.getElements().entrySet().stream().filter( (e) -> ! e.getKey().getLocalPart().endsWith( SUBSTITUTION_GROUP_HEAD_ENDING ) ).collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
			System.out.println( "Element names: " + elements.keySet() );
			for ( final XmlSchemaElement elDecl : elements.values() ) {
				final Optional<Entity> optEntity = Optional.ofNullable( elDecl.getMetaInfoMap() ).map( (m) -> (Entity) m.get( EntityLinkExtensionDeserializer.CFLINK_ENTITY_QNAME ) ); 
				optEntity.ifPresent( (e) -> basicEntitiesByName.remove( e.getPhysicalName() ) );
				if ( !optEntity.isPresent() ) {
					System.out.println( "No entity reference: " + elDecl.getName() );
				}
				
				final XmlSchemaComplexType type = (XmlSchemaComplexType) elDecl.getSchemaType();
				final XmlSchemaComplexContentExtension content = (XmlSchemaComplexContentExtension) type.getContentModel().getContent();
				
				final XmlSchemaSequence sequence = (XmlSchemaSequence) content.getParticle();
				if ( sequence != null ) {
					for ( final XmlSchemaSequenceMember member : sequence.getItems() ) {
						if ( member instanceof XmlSchemaElement ) {
							final XmlSchemaElement elDecl2 = (XmlSchemaElement) member;
							if ( elDecl2.getMinOccurs() > 0 ) {
								System.out.println( "Mandatory particle in contents: " + elDecl.getName() + "/" + elDecl2.getName() );
							}
							final Optional<Map<Object, Object>> optMetainfoMap = Optional.ofNullable( elDecl2.getMetaInfoMap() );
							optMetainfoMap.map( (m) -> (Attribute) m.get( AttributeLinkExtensionDeserializer.CFLINK_ATTRIBUTE_QNAME ) ).ifPresent( (a) -> basicAttributesByName.remove( a.getPhysicalName() ) );
							if ( !optMetainfoMap.isPresent() ) {
								System.out.println( "No annotation for: " + elDecl.getName() + "/" + elDecl2.getName() );
							}
						} else if ( member instanceof XmlSchemaGroupRef ) {
							// TODO
						}
					}
				} else {
					System.out.println( "No content particle for " + elDecl.getName() );
				}
			}
			
			System.out.println( "-----------------" );
			for ( final String e : basicEntitiesByName.keySet() ) {
				System.out.println( "Entity '" + e + "' is not represented in the schema" );
			}
			
			System.out.println( "-----------------" );
			for ( final String a : basicAttributesByName.keySet() ) {
				System.out.println( "Attribute '" + a + "' is not represented in the schema" );
			}
			
			System.out.println( "-----------------" );
			System.out.println( "Totals: " + basicEntitiesByName.size() + " unrepresented entities, " + basicAttributesByName.size() + " unrepresented attributes" );

		} catch ( final Throwable t ) {
			t.printStackTrace( System.err );
		}
	}

	public static Stream<Attribute> streamOfNonDeprecatedAttributes( final Entity e ) {
		return e.getAttributes().stream().filter( (a) -> ToadModelParser.extractInfo( "deprecated", a.getNotes(), null ) == null );
	}

	protected static class MyExtensionRegistry extends ExtensionRegistry {
		protected MyExtensionRegistry( final Model model ) {
			super();
			registerDeserializer( EntityLinkExtensionDeserializer.CFLINK_ENTITY_QNAME, new EntityLinkExtensionDeserializer( model ) );
			registerDeserializer( AttributeLinkExtensionDeserializer.CFLINK_ATTRIBUTE_QNAME, new AttributeLinkExtensionDeserializer( model ) );
		}
	}
	
	protected static class EntityLinkExtensionDeserializer implements ExtensionDeserializer {

		public static final QName CFLINK_ENTITY_QNAME = new QName( CFLINK_NS_URI, "entity" );

		private final Model model;
		
		public EntityLinkExtensionDeserializer( final Model model ) {
			this.model = model;
		}

		@Override
		public void deserialize( final XmlSchemaObject obj, final QName qname, final Node node ) {
			final Attr attr = (Attr) node;
			final String name = attr.getValue();
			final Entity entity = model.getEntityBy( name );
			if ( entity != null ) {
				obj.addMetaInfo( CFLINK_ENTITY_QNAME, entity );
			} else {
				System.err.println( "Entity '" + name + "' not found in the model" );
			}
		}
		
	}

	protected static class AttributeLinkExtensionDeserializer implements ExtensionDeserializer {

		public static final QName CFLINK_ATTRIBUTE_QNAME = new QName( CFLINK_NS_URI, "attribute" );

		private final Model model;
		
		public AttributeLinkExtensionDeserializer( final Model model ) {
			this.model = model;
		}

		@Override
		public void deserialize( final XmlSchemaObject obj, final QName qname, final Node node ) {
			final Attr attr = (Attr) node;
			final String name = attr.getValue();
			final Attribute attribute = model.getAttributeBy( name );
			if ( attribute != null ) {
				obj.addMetaInfo( CFLINK_ATTRIBUTE_QNAME, attribute );
			} else {
				System.err.println( "Attribute '" + name + "' not found in the model" );
			}
		}
		
	}

}

