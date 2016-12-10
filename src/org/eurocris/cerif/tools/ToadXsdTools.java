package org.eurocris.cerif.tools;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eurocris.cerif.model.CERIFEntityType;
import org.eurocris.cerif.model.Entity;
import org.eurocris.cerif.model.Model;
import org.eurocris.cerif.model.toad.ToadModelParser;
import org.eurocris.cerif.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ToadXsdTools {

	public static final String CFLINK_NS_URI = "http://eurocris.org/cerif/annotations#";
	
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
			
			final Map<String, Entity> basicEntitiesByName = new LinkedHashMap<>();
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
						break;
					default:
						break;
					}
				} else {
					System.err.println( "Entity '" + e.getPhysicalName() + "' doesn't have a type" );
				}
			}

			final File schemaFile = new File(line.getOptionValue('s'));
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware( true );
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document schema = dBuilder.parse( schemaFile );			
			final Element schemaRootEl = schema.getDocumentElement();
			schemaRootEl.normalize();
			for ( final Element e : XMLUtils.getChildElements( schemaRootEl ) ) {
				if ( XMLConstants.W3C_XML_SCHEMA_NS_URI.equals( e.getNamespaceURI() ) ) {
					if ( "element".equals( e.getLocalName() ) ) {
						final String entityName = e.getAttributeNS( CFLINK_NS_URI, "entity" );
						if ( basicEntitiesByName.containsKey( entityName ) ) {
							basicEntitiesByName.remove( entityName );
							// ok
						} else {
							System.out.println( "Entity '" + entityName + "' is not known" );
						}
					}
				}
			}
			
			for ( final String e : basicEntitiesByName.keySet() ) {
				System.out.println( "Entity '" + e + "' is not represented in the schema" );
			}
			

		} catch ( final Throwable t ) {
			t.printStackTrace( System.err );
		}
	}

}
