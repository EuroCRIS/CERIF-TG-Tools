package org.eurocris.cerif.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.eurocris.cerif.profile.def.Annotated;
import org.eurocris.cerif.profile.def.Annotation;
import org.eurocris.cerif.profile.def.CERIFProfile;
import org.eurocris.cerif.profile.def.CERIFProfile.Entities.Entity;
import org.eurocris.cerif.profile.def.CERIFProfile.Entities.Entity.Classification;
import org.eurocris.cerif.profile.def.CERIFProfile.Entities.Entity.Identifier;
import org.eurocris.cerif.profile.def.CERIFProfile.Entities.Entity.Link;
import org.eurocris.cerif.profile.def.OpenAttrs;
import org.eurocris.cerif.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class CerifXmlSchemaExpander {

	private static final String CF_LINK_NSURI = "https://w3id.org/cerif/annotations#";
	private static final String XS_NSURI = "http://www.w3.org/2001/XMLSchema";
	private static final String XSI_NSURI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String CF_PROCESS_NSPFX = "cfprocess";
	public static final String CF_PROCESS_NSURI = "https://w3id.org/cerif/preprocessing#";
	public static final String CTLFILE_NSURI = "https://w3id.org/cerif/profile";
	public static final String TEMPLATE_XSD_PATH = "/xsd/cerif-template.xsd";
	private static final String XSD_CERIF_PROFILE_DEFINITION_XSD = "/xsd/cerif-profile-definition.xsd";

	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private final Transformer transformer;
	private final XPathFactory xpathFactory = XPathFactory.newInstance();

	public static void main( final String[] args ) {
		try {
			final Options options = new Options();
			options.addOption( "d", "defFile", true, "path to the CERIF profile definition file" );
			options.addOption( "o", "output", true, "full path where the profile XML Schema should be placed" );
			options.addOption( "h", "help", true, "this help message" );

			final CommandLine line = new DefaultParser().parse( options, args );

			if ( line.hasOption( "h" ) || !( line.hasOption( "d" ) && line.hasOption( "o" ) ) ) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "CerifXmlSchemaExpander", options );
				// print out the help
				System.exit( line.hasOption( "h" ) ? 0 : 1 );
			}

			final File defFile = new File( line.getOptionValue( 'd' ) );
			final CerifXmlSchemaExpander cerifXmlSchemaExpander = new CerifXmlSchemaExpander( defFile );

			final File outFile = new File( line.getOptionValue( 'o' ) );
			final File outDir = outFile.getParentFile();
			if ( outDir.exists() && !outDir.isDirectory() ) {
				System.out.println( line.getOptionObject( 'o' ) + "is not a folder" );
				// print out the help
				System.exit( 1 );
			}
			if ( !outDir.exists() ) {
				outDir.mkdirs();
			}

			cerifXmlSchemaExpander.expandInto( outFile );

		} catch ( final Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}

	private final File defFile;

	private final SimpleNamespaceContext nsCtx = new SimpleNamespaceContext();
	{
		nsCtx.bind( "cfprocess", CF_PROCESS_NSURI );
		nsCtx.bind( "cflink", CF_LINK_NSURI );
		nsCtx.bind( "xs", XS_NSURI );
		nsCtx.bind( "xml", "http://www.w3.org/XML/1998/namespace" );
		nsCtx.bind( "", CERIFProfile.class.getAnnotation( XmlRootElement.class ).namespace() );
	}

	private final CERIFProfile def;
	private final Map<String, Entity> entityByUri = new LinkedHashMap<>();
	private final Binder<Node> marshaller;

	private CerifXmlSchemaExpander( final File defFile ) throws TransformerConfigurationException, JAXBException, SAXException, IOException, ParserConfigurationException {
		this.defFile = defFile;
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

		final JAXBContext jc = JAXBContext.newInstance( CERIFProfile.class.getPackage().getName() );
		final Unmarshaller u = jc.createUnmarshaller();
		final SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
		final Schema s = sf.newSchema( getClass().getResource( XSD_CERIF_PROFILE_DEFINITION_XSD ) );
		u.setSchema( s );
		final Document defDoc = readIn( defFile );
		def = (CERIFProfile) u.unmarshal( defDoc );

		for ( final Entity e : def.getEntities().getEntity() ) {
			entityByUri.put( e.getUri(), e );
		}

		nsCtx.bind( "", def.getTargetNamespace().getUri() );
		marshaller = jc.createBinder();
		marshaller.setProperty( "com.sun.xml.bind.namespacePrefixMapper", nsCtx );
	}

	public void expandInto( final File outFile ) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerException, JAXBException {
		final Document doc = readIn( getClass().getResourceAsStream( TEMPLATE_XSD_PATH ) );
		final Element elSchemaRoot = doc.getDocumentElement();
		final Element firstImportElement = (Element) createXPath().evaluate( "xs:import[1]", elSchemaRoot, XPathConstants.NODE );
		shiftNamespace( doc );
		changeAnnotation( doc );
		filterEntities( doc );
		expandUnaryClassifications( doc, defFile.getParentFile(), outFile.getParentFile() );
		expandIdentifiers( doc );
		expandTails( doc );
		addSchemaHeads( doc, firstImportElement );
		removeProcessingInstructions( doc );
		writeOut( doc, outFile );
		System.out.println( "Written out file " + outFile + " of " + outFile.length() + " B" );
	}

	protected void removeProcessingInstructions( final Document doc ) throws XPathExpressionException {
		final Element elSchemaRoot = doc.getDocumentElement();
		for ( final Node n1 : XMLUtils.asList( (NodeList) createXPath().evaluate( "descendant-or-self::xs:*/@cfprocess:*", elSchemaRoot, XPathConstants.NODESET ) ) ) {
			removeFromTree( n1 );
		}
	}

	protected void expandTails( final Document doc ) throws XPathExpressionException, JAXBException {
		final Element elSchemaRoot = doc.getDocumentElement();
		final NodeList nodes = (NodeList) createXPath().evaluate( "descendant-or-self::xs:group[ @ref = '__TheRestGroup' ]", elSchemaRoot, XPathConstants.NODESET );
		for ( final Element el1 : XMLUtils.asElementList( nodes ) ) {
			final String entityName = createXPath().evaluate( "ancestor::xs:element/@cflink:entity[1]", el1 );
			final Entity entityDef = entityByUri.get( entityName );
			if ( entityDef != null ) {
				for ( final Link link : entityDef.getLink() ) {
					final Element el2 = el1.getOwnerDocument().createElementNS( XS_NSURI, "xs:element" );
					combineOccurs( el2, "minOccurs", el1, link.getMinOccurs() );
					combineOccurs( el2, "maxOccurs", el1, link.getMaxOccurs() );
					el2.setAttribute( "name", link.getName() );
					el2.setAttributeNS( CF_LINK_NSURI, "cflink:link", link.getLink() );
					el1.getParentNode().insertBefore( el2, el1 );
					insertNewlineBefore( el1 );
					addAnnotation( el2, link );

					final Element el3 = createXsChild( el2, "complexType" );
					final Element el4 = createXsChild( el3, "complexContent" );
					final Element el5 = createXsChild( el4, "extension" );
					el5.setAttribute( "base", "cfLink__BaseType" );
					final Element el6 = createXsChild( el5, "choice" );
					for ( final String tgt : link.getTarget() ) {
						final Element el7 = createXsChild( el6, "element" );
						el7.setAttribute( "ref", tgt );
					}
				}
				for ( final Object x : entityDef.getNestedParticle() ) {
					marshalBefore( el1, x );
					insertNewlineBefore( el1 );
				}
			} else {
				throw new IllegalStateException( "No entity " + entityName );
			}
		}
	}

	protected void addSchemaHeads( final Document doc, final Element firstImportElement ) throws XPathExpressionException, JAXBException {
		final Element elSchemaRoot = doc.getDocumentElement();
		for ( final OpenAttrs x : def.getIncludeOrImportOrRedefine() ) {
			marshalBefore( firstImportElement, x );
			insertNewlineBefore( firstImportElement );
		}
		final NodeList nodes = (NodeList) createXPath().evaluate( "descendant-or-self::xs:group[ @ref = '__TheRestGroup' ]", elSchemaRoot, XPathConstants.NODESET );
		for ( final Element el1 : XMLUtils.asElementList( nodes ) ) {
			final String entityName = createXPath().evaluate( "ancestor::xs:element/@cflink:entity[1]", el1 );
			final Entity entityDef = entityByUri.get( entityName );
			if ( entityDef != null ) {
				for ( final OpenAttrs x : entityDef.getIncludeOrImportOrRedefine() ) {
					marshalBefore( firstImportElement, x );
					insertNewlineBefore( firstImportElement );
				}
			}
		}
	}

	protected Element createXsChild( final Element parentEl, final String localName ) {
		try {
			if ( !parentEl.hasChildNodes() ) {
				parentEl.appendChild( parentEl.getOwnerDocument().createTextNode( "\n" ) );
			}
			return (Element) parentEl.appendChild( parentEl.getOwnerDocument().createElementNS( XS_NSURI, "xs:" + localName ) );
		} finally {
			parentEl.appendChild( parentEl.getOwnerDocument().createTextNode( "\n" ) );
		}
	}

	protected void shiftNamespace( final Document doc ) {
		final Element elSchemaRoot = doc.getDocumentElement();
		final String targetNamespaceUri = def.getTargetNamespace().getUri();
		elSchemaRoot.setAttribute( "targetNamespace", targetNamespaceUri );
		elSchemaRoot.setAttribute( "xmlns", targetNamespaceUri );

		final Attr schemaLocationAttr = elSchemaRoot.getAttributeNodeNS( XSI_NSURI, "schemaLocation" );
		final String loc1 = schemaLocationAttr.getValue();
		final String loc2 = loc1.replace( " https://w3id.org/cerif/annotations# ./cerif-model-annotations.xsd", "" );
		schemaLocationAttr.setValue( loc2 );
	}

	protected void changeAnnotation( final Document doc ) throws JAXBException {
		final Element elSchemaRoot = doc.getDocumentElement();
		addAnnotation( elSchemaRoot, def );
	}

	protected void filterEntities( final Document doc ) throws JAXBException {
		final Element schemaRoot = doc.getDocumentElement();
		final List<Node> nodesToRemove = new ArrayList<>();
		for ( final Element el2 : XMLUtils.asElementList( schemaRoot.getElementsByTagNameNS( XS_NSURI, "element" ) ) ) {
			final String cfLinkEntity = el2.getAttributeNS( CF_LINK_NSURI, "entity" );
			if ( StringUtils.isNotBlank( cfLinkEntity ) && !entityByUri.containsKey( cfLinkEntity ) ) {
				System.out.println( "Leaving out entity " + cfLinkEntity );
				nodesToRemove.add( el2 );
			} else {
				final Entity entity = entityByUri.get( cfLinkEntity );
				if ( entity != null ) {
					addAnnotation( el2, entity );
					final String leaveOut1 = entity.getLeaveOut();
					if ( StringUtils.isNotBlank( leaveOut1 ) ) {
						final Collection<String> leaveOut = Arrays.asList( leaveOut1.split( "\\s" ) );

						final Element el3 = XMLUtils.getSingleElement( el2, "xs:complexType" );
						if ( el3 != null ) {
							final Element el4 = XMLUtils.getSingleElement( el3, "xs:complexContent" );
							final Element el5 = XMLUtils.getSingleElement( el4, "xs:extension" );
							final Element el6 = XMLUtils.getSingleElement( el5, "xs:sequence" );

							for ( final Element elx : XMLUtils.asElementList( el6.getElementsByTagNameNS( XS_NSURI, "*" ) ) ) {
								final String elxName = elx.getAttribute( "name" ) + elx.getAttribute( "ref" );
								if ( leaveOut.contains( elxName ) ) {
									System.out.println( "Leaving out " + elxName + " from entity " + cfLinkEntity );
									nodesToRemove.add( elx );
								}
							}
						}
					}
				}
			}
		}
		for ( final Node node : nodesToRemove ) {
			removeFromTree( node );
		}
	}

	protected void expandUnaryClassifications( final Document doc, final File baseDir, final File targetDir ) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, JAXBException {
		final Pattern pattern1 = Pattern.compile( "Classification\\((.*)\\)" );

		final Element elSchemaRoot = doc.getDocumentElement();
		final Element firstImportElement = (Element) createXPath().evaluate( "xs:import[1]", elSchemaRoot, XPathConstants.NODE );

		final NodeList nodes = (NodeList) createXPath().evaluate( "descendant-or-self::xs:element[ @cfprocess:expandBy and not( @cflink:identifier ) ]", elSchemaRoot, XPathConstants.NODESET );
		for ( final Element el1 : XMLUtils.asElementList( nodes ) ) {
			final Attr expandByAttr = el1.getAttributeNodeNS( CF_PROCESS_NSURI, "expandBy" );
			final String expandByDef = expandByAttr.getValue();
			final Matcher matcher1 = pattern1.matcher( expandByDef );
			if ( matcher1.matches() ) {
				final String elementName = el1.getAttribute( "name" );
				final String classSchemeType = matcher1.group( 1 );
				final String entityName = createXPath().evaluate( "ancestor::xs:element/@cflink:entity[1]", el1 );
				System.out.println( "Processing " + elementName + " in entity " + entityName );
				final Entity entityDef = entityByUri.get( entityName );
				boolean noInstructions = true;
				final List<Classification> classificationExpansions = ( entityDef != null ) ? entityDef.getClassification() : null;
				if ( classificationExpansions != null ) {
					for ( final Classification classification : classificationExpansions ) {
						if ( classSchemeType.equals( classification.getKind() ) ) {
							System.out.println( "Classification " + classification.getSchema() + " for " + el1.getLocalName() + " " + classSchemeType + " in " + entityName );
							makeEnumReference( firstImportElement, el1, targetDir, classification );
							noInstructions = false;
						}
					}
				}
				if ( noInstructions ) {
					System.err.println( "Unsupported cflink:entity instruction: " + entityName );
				} else {
					removeFromTree( expandByAttr );
					removeFromTree( el1 );
				}
			} else if ( "#entities".equals( expandByDef ) ) {
				final Node preNode2 = el1.getPreviousSibling();
				final NodeList nodes2 = (NodeList) createXPath().evaluate( "xs:element", elSchemaRoot, XPathConstants.NODESET );
				final int n2 = nodes2.getLength();
				for ( int i2 = 0; i2 < n2; ++i2 ) {
					final Element el2 = (Element) nodes2.item( i2 );
					final String name2 = el2.getAttribute( "name" );
					final Element el3 = el1.getOwnerDocument().createElementNS( XS_NSURI, "xs:element" );
					el3.setAttribute( "ref", name2 );
					if ( preNode2 != null && preNode2.getNodeType() == Node.TEXT_NODE ) {
						el1.getParentNode().insertBefore( preNode2.cloneNode( true ), el1 );
					}
					el1.getParentNode().insertBefore( el3, el1 );
				}
				removeFromTree( el1 );
				removeFromTree( preNode2 );
				removeFromTree( expandByAttr );
			} else {
				System.err.println( "Unsupported cfproccess:expandBy instruction: " + expandByDef );
			}
		}

	}

	protected void makeEnumReference( final Element firstImportElement, final Element el1, final File enumSchemaTargetLocation, final Classification ucDef ) throws SAXException, IOException, ParserConfigurationException, JAXBException {
		final File enumSchemaTargetFile = new File( enumSchemaTargetLocation, ucDef.getSchema() );
		final Document enumSchemaDocument = readIn( enumSchemaTargetFile );
		final Element enumSchemaRootEl = enumSchemaDocument.getDocumentElement();
		final String enumNsUri = enumSchemaRootEl.getAttribute( "targetNamespace" );
		final String enumElName = ( (Element) enumSchemaRootEl.getElementsByTagNameNS( XS_NSURI, "element" ).item( 0 ) ).getAttribute( "name" );

		final Element el2 = el1.getOwnerDocument().createElementNS( XS_NSURI, "xs:element" );
		combineOccurs( el2, "minOccurs", el1, ucDef.getMinOccurs() );
		combineOccurs( el2, "maxOccurs", el1, ucDef.getMaxOccurs() );
		final String el1CflinkLink = el1.getAttributeNS( CF_LINK_NSURI, "link" );
		if ( !"".equals( el1CflinkLink ) ) {
			el2.setAttributeNS( CF_LINK_NSURI, "cflink:link", el1CflinkLink );
		}
		el2.setAttribute( "ref", enumElName );
		el2.setAttribute( "xmlns", enumNsUri );
		addAnnotation( el2, ucDef );
		placeBefore( el2, el1 );

		final Element el3 = firstImportElement.getOwnerDocument().createElementNS( XS_NSURI, "xs:import" );
		el3.setAttribute( "namespace", enumNsUri );
		el3.setAttribute( "schemaLocation", ucDef.getSchema() );
		placeBefore( el3, firstImportElement );
	}

	protected void placeBefore( final Element newEl, final Element refEl ) {
		final Node preNode3 = refEl.getPreviousSibling();
		if ( preNode3 != null && preNode3.getNodeType() == Node.TEXT_NODE ) {
			refEl.getParentNode().insertBefore( preNode3.cloneNode( true ), preNode3 );
			refEl.getParentNode().insertBefore( newEl, preNode3 );
		} else {
			refEl.getParentNode().insertBefore( newEl, refEl );
		}
	}

	protected void expandIdentifiers( final Document doc ) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		final Element elSchemaRoot = doc.getDocumentElement();

		final NodeList nodes = (NodeList) createXPath().evaluate( "descendant-or-self::xs:element[ @cfprocess:expandBy and @cflink:identifier ]", elSchemaRoot, XPathConstants.NODESET );
		for ( final Element el1 : XMLUtils.asElementList( nodes ) ) {
			final Attr expandByAttr = el1.getAttributeNodeNS( CF_PROCESS_NSURI, "expandBy" );
			final String elementName = el1.getAttribute( "name" );
			final String entityName = createXPath().evaluate( "ancestor::xs:element/@cflink:entity[1]", el1 );
			System.out.println( "Processing " + elementName + " in entity " + entityName );
			final Entity entityDef = entityByUri.get( entityName );
			boolean noInstructions = true;
			final List<Identifier> identifierExpansions = ( entityDef != null ) ? entityDef.getIdentifier() : null;
			if ( identifierExpansions != null ) {
				for ( final Identifier identifier : identifierExpansions ) {
					System.out.println( "Identifier " + identifier.getName() + " for " + el1.getLocalName() + " in " + entityName );
					final Element el2 = el1.getOwnerDocument().createElementNS( XS_NSURI, "xs:element" );
					el2.setAttribute( "name", identifier.getName() );
					combineOccurs( el2, "minOccurs", el1, identifier.getMinOccurs() );
					combineOccurs( el2, "maxOccurs", el1, identifier.getMaxOccurs() );
					el2.setAttribute( "type", "cfString__Type" );
					el2.setAttributeNS( CF_LINK_NSURI, "cflink:link", identifier.getLink() );
					el2.setAttributeNS( CF_LINK_NSURI, "cflink:identifier", "true" );
					el1.getParentNode().insertBefore( el2, el1 );
					noInstructions = false;
				}
			}
			if ( noInstructions ) {
				System.err.println( "Unexpandable identifier instruction: " + entityName );
			} else {
				removeFromTree( expandByAttr );
				removeFromTree( el1 );
			}
		}
	}

	protected Element addAnnotation( final Element el, final Annotated a1 ) throws JAXBException {
		final Annotation a = a1.getAnnotation();
		if ( a != null ) {
			final Element ex = XMLUtils.getSingleElement( el, "xs:annotation" );
			final Element e2 = marshalAsFirstChild( el, a );
			if ( ex != null ) {
				el.removeChild( ex );
			}
			return e2;
		} else {
			return null;
		}
	}

	protected void insertNewlineBefore( final Element el1 ) {
		el1.getParentNode().insertBefore( el1.getOwnerDocument().createTextNode( "\n\n" ), el1 );
	}

	protected Element marshalAsLastChild( final Element el2, final Object a ) throws JAXBException {
		marshaller.marshal( a, el2 );
		final Element e = (Element) el2.getLastChild();
		e.removeAttribute( "xmlns" );
		return e;
	}

	protected Element marshalAsFirstChild( final Element el2, final Object a ) throws JAXBException {
		final Node origFirstChild = el2.getFirstChild();
		final Element el3 = marshalAsLastChild( el2, a );
		return (Element) el2.insertBefore( el3, origFirstChild );
	}

	protected Element marshalBefore( final Element el1, final Object a ) throws JAXBException {
		final Element el2 = (Element) el1.getParentNode();
		final Element el3 = marshalAsLastChild( el2, a );
		return (Element) el2.insertBefore( el3, el1 );
	}

	protected void combineOccurs( final Element targetEl, final String type, final Element srcEl, final Object overrideValue ) {
		final String overrideValue1 = Objects.toString( overrideValue, "" );
		final String effOccurs = ( StringUtils.isBlank( overrideValue1 ) ) ? srcEl.getAttribute( type ) : overrideValue1;
		if ( ( !StringUtils.isBlank( effOccurs ) ) && ( !"1".equals( effOccurs ) ) ) {
			targetEl.setAttribute( type, effOccurs );
		}
	}

	protected void removeFromTree( final Node node ) {
		if ( node instanceof Attr ) {
			final Attr attr = (Attr) node;
			attr.getOwnerElement().removeAttributeNode( attr );
		} else {
			final Node parentNode = node.getParentNode();
			final Node previousNode = node.getPreviousSibling();
			parentNode.removeChild( node );
			if ( previousNode instanceof Text ) {
				final Text previousText = (Text) previousNode;
				if ( previousText.isElementContentWhitespace() ) {
					parentNode.removeChild( previousNode );
				}
			}
		}
	}

	protected XPath createXPath() {
		final XPath xPath = xpathFactory.newXPath();
		xPath.setNamespaceContext( nsCtx );
		return xPath;
	}

	protected void writeOut( final Document doc, final File outputXsdFile ) throws TransformerException {
		transformer.transform( new DOMSource( doc ), new StreamResult( outputXsdFile ) );
	}

	protected Document readIn( final File file ) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbf = createDocumentBuilderFactory();
		return dbf.newDocumentBuilder().parse( file );
	}

	protected Document readIn( final InputStream stream ) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbf = createDocumentBuilderFactory();
		return dbf.newDocumentBuilder().parse( stream );
	}

	protected DocumentBuilderFactory createDocumentBuilderFactory() {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware( true );
		return dbf;
	}

}

class SimpleNamespaceContext extends NamespacePrefixMapper implements NamespaceContext {

	private final Map<String, String> prefixToUri = new HashMap<>();
	private final Map<String, String> uriToPrefix = new HashMap<>();

	@Override
	public String getNamespaceURI( final String prefix ) {
		return prefixToUri.get( prefix );
	}

	@Override
	public String getPrefix( final String namespaceURI ) {
		@SuppressWarnings( "unchecked")
		final Iterator<String> i = getPrefixes( namespaceURI );
		return ( i.hasNext() ) ? i.next() : null;
	}

	@SuppressWarnings( "rawtypes")
	@Override
	public Iterator getPrefixes( final String namespaceURI ) {
		return prefixToUri.entrySet().stream().filter( ( e ) -> e.getValue().equals( namespaceURI ) ).map( ( e ) -> e.getKey() ).iterator();
	}

	public void bind( final String prefix, final String namespaceURI ) {
		prefixToUri.putIfAbsent( prefix, namespaceURI );
		uriToPrefix.put( namespaceURI, prefix );
	}

	public void unbind( final String prefix ) {
		uriToPrefix.remove( prefixToUri.remove( prefix ) );
	}

	@Override
	public String getPreferredPrefix( final String uri, final String suggestion, final boolean requirePrefix ) {
		return StringUtils.defaultString( suggestion, StringUtils.defaultString( uriToPrefix.get( uri ) ) );
	}

}
