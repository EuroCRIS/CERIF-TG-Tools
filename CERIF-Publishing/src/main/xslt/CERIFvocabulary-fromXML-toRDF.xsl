<?xml version="1.0" encoding="UTF-8"?>
<!--
This stylesheet is used to transform a CERIF XML vocabulary file to a CERIF RDF vocabulary
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:x="urn:xmlns:org:eurocris:cerif-1.5-1"
	exclude-result-prefixes="x">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="no" standalone="yes"/>

	<xsl:template match="/">
		<xsl:for-each select="x:CERIF/x:cfClassScheme">
		<rdf:RDF xmlns="https://w3id.org/cerif/vocab/"
			xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:skos="http://www.w3.org/2004/02/skos/core#"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
			xmlns:xml="http://www.w3.org/XML/1998/namespace">
			
			<xsl:variable name="schemeShortName" select="translate(x:cfName,' ', '')"/>

			<!--class scheme-->
			<rdf:Description>
				<xsl:attribute name="rdf:about">
					<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
					<xsl:value-of select="$schemeShortName"/>
				</xsl:attribute>
				<rdf:type rdf:resource="https://w3id.org/cerif/model#cfClassificationScheme"/>
				<rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
				<dc:identifier><xsl:value-of select="x:cfClassSchemeId"/></dc:identifier>
				<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
					<xsl:attribute name="xml:lang">
						<xsl:value-of select="x:cfName/@cfLangCode"/>
					</xsl:attribute>
					<xsl:value-of select="x:cfName"/>
				</rdfs:label>
				<dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
					<xsl:attribute name="xml:lang">
						<xsl:value-of select="x:cfDescr/@cfLangCode"/>
					</xsl:attribute>
					<xsl:value-of select="x:cfDescr"/>
				</dc:description>
				<xsl:for-each select="x:cfClass">
					<skos:hasTopConcept>
						<xsl:attribute name="rdf:resource">
							<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
							<xsl:value-of select="$schemeShortName"/>
							<xsl:text>#</xsl:text>
							<xsl:value-of select="translate(x:cfTerm,' ', '')"/>
						</xsl:attribute>
					</skos:hasTopConcept>
				</xsl:for-each>
			</rdf:Description>
			
			<!--classes-->
			<xsl:for-each select="x:cfClass">
				<rdf:Description>
					<xsl:attribute name="rdf:about">
						<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
						<xsl:value-of select="$schemeShortName"/>
						<xsl:text>#</xsl:text>
						<xsl:value-of select="translate(x:cfTerm,' ', '')"/>
					</xsl:attribute>
					<rdf:type rdf:resource="https://w3id.org/cerif/model#cfClassification"/>
					<rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
					<skos:topConceptOf>
						<xsl:attribute name="rdf:resource">
							<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
							<xsl:value-of select="$schemeShortName"/>
						</xsl:attribute>
					</skos:topConceptOf>
					<dc:identifier><xsl:value-of select="x:cfClassId"/></dc:identifier>
					<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
						<xsl:attribute name="xml:lang">
							<xsl:value-of select="x:cfTerm/@cfLangCode"/>
						</xsl:attribute>
						<xsl:value-of select="x:cfTerm"/>
					</rdfs:label>
					<xsl:if test="x:cfDescr">
						<dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="x:cfDescr/@cfLangCode"/>
							</xsl:attribute>
							<xsl:value-of select="x:cfDescr"/>
						</dc:description>
					</xsl:if>
					<xsl:if test="x:cfDef">
						<skos:definition rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="x:cfDef/@cfLangCode"/>
							</xsl:attribute>
							<xsl:value-of select="x:cfDef"/>
						</skos:definition>
					</xsl:if>
					
					<xsl:for-each select="x:cfTermSrc | x:cfDescrSrc | x:cfDefSrc">
						<dc:source rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="@cfLangCode"/>
							</xsl:attribute>
							<xsl:value-of select="text()"/>
						</dc:source>
					</xsl:for-each>
				
				</rdf:Description>
			</xsl:for-each>

		</rdf:RDF>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet> 