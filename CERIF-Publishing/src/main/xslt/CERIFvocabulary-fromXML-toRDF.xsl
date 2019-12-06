<?xml version="1.0" encoding="UTF-8"?>
<!--
This stylesheet is used to transform a CERIF XML vocabulary file to a CERIF RDF vocabulary
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="no" standalone="yes"/>

	<xsl:template match="/">
		<xsl:for-each select="CERIF/cfClassScheme">
		<rdf:RDF xmlns="https://w3id.org/cerif/vocab/"
			xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:skos="http://www.w3.org/2004/02/skos/core#"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
			xmlns:xml="http://www.w3.org/XML/1998/namespace">
			
			<xsl:variable name="schemeShortName" select="translate(cfName,' ', '')"/>

			<!--class scheme-->
			<rdf:Description>
				<xsl:attribute name="rdf:about">
					<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
					<xsl:value-of select="$schemeShortName"/>
				</xsl:attribute>
				<rdf:type rdf:resource="https://w3id.org/cerif/model#cfClassificationScheme"/>
				<rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
				<dc:identifier><xsl:value-of select="cfClassSchemeId"/></dc:identifier>
				<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
					<xsl:attribute name="xml:lang">
						<xsl:value-of select="cfName/@cfLangCode"/>
					</xsl:attribute>
					<xsl:value-of select="cfName"/>
				</rdfs:label>
				<dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
					<xsl:attribute name="xml:lang">
						<xsl:value-of select="cfDescr/@cfLangCode"/>
					</xsl:attribute>
					<xsl:value-of select="cfDescr"/>
				</dc:description>
				<xsl:for-each select="cfClass">
					<skos:hasTopConcept>
						<xsl:attribute name="rdf:resource">
							<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
							<xsl:value-of select="$schemeShortName"/>
							<xsl:text>#</xsl:text>
							<xsl:value-of select="translate(cfTerm,' ', '')"/>
						</xsl:attribute>
					</skos:hasTopConcept>
				</xsl:for-each>
			</rdf:Description>
			
			<!--classes-->
			<xsl:for-each select="cfClass">
				<rdf:Description>
					<xsl:attribute name="rdf:about">
						<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
						<xsl:value-of select="$schemeShortName"/>
						<xsl:text>#</xsl:text>
						<xsl:value-of select="translate(cfTerm,' ', '')"/>
					</xsl:attribute>
					<rdf:type rdf:resource="https://w3id.org/cerif/model#cfClassification"/>
					<rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
					<skos:topConceptOf>
						<xsl:attribute name="rdf:resource">
							<xsl:text>https://w3id.org/cerif/vocab/</xsl:text>
							<xsl:value-of select="$schemeShortName"/>
						</xsl:attribute>
					</skos:topConceptOf>
					<dc:identifier><xsl:value-of select="cfClassId"/></dc:identifier>
					<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
						<xsl:attribute name="xml:lang">
							<xsl:value-of select="cfTerm/@cfLangCode"/>
						</xsl:attribute>
						<xsl:value-of select="cfTerm"/>
					</rdfs:label>
					<xsl:if test="cfDescr">
						<dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="cfDescr/@cfLangCode"/>
							</xsl:attribute>
							<xsl:value-of select="cfDescr"/>
						</dc:description>
					</xsl:if>
					<xsl:if test="cfDef">
						<skos:definition rdf:datatype="http://www.w3.org/2001/XMLSchema#string">
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="cfDef/@cfLangCode"/>
							</xsl:attribute>
							<xsl:value-of select="cfDef"/>
						</skos:definition>
					</xsl:if>
					
					<xsl:for-each select="cfTermSrc | cfDescrSrc | cfDefSrc">
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