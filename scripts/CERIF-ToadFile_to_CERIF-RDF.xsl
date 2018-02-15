<?xml version="1.0"?>
<!--
This stylesheet is used to transform the txp file -corresponding to the TOAD data modeler- to the full RDF schema for CERIF
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="no" standalone="yes" />
	<xsl:template match="/">
		<rdf:RDF xmlns="https://w3id.org/cerif/model#"
			xml:base="https://w3id.org/cerif/model"
			xmlns:dct="http://purl.org/dc/terms/"
			xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
			xmlns:owl="http://www.w3.org/2002/07/owl#"
			xmlns:xml="http://www.w3.org/XML/1998/namespace"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
			xmlns:vann="http://purl.org/vocab/vann/"
			xmlns:vs="http://www.w3.org/2003/06/sw-vocab-status/ns#">
			<owl:Ontology rdf:about="https://w3id.org/cerif/model">
				<rdfs:comment xml:lang="en">This document describes the CERIF data model.</rdfs:comment>
				<rdfs:comment xml:lang="en">This version is experimental.</rdfs:comment>
				<vann:preferredNamespaceUri>https://w3id.org/cerif/model#</vann:preferredNamespaceUri>
				<dct:creator>Laurent Remy</dct:creator>
				<vann:preferredNamespacePrefix>cerif</vann:preferredNamespacePrefix>
				<dct:creator>Jan Dvorak</dct:creator>
			</owl:Ontology>

			<!--
			********************
			basic structure
			********************
			-->
			<owl:Class rdf:about="https://w3id.org/cerif/model#Entity">
				<rdfs:comment xml:lang="en">CERIF root entity is the superclass of all CERIF data model entities.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF root entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#BaseEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:comment xml:lang="en">CERIF base entity is the superclass of all CERIF data model entities used to express the basic concepts of research activities, i.e. projects, persons and organisation units.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF base entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#ResultEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:comment xml:lang="en">CERIF result entity is the superclass of all CERIF data model entities used to express results of the research activities.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF result entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#InfrastructureEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:comment xml:lang="en">CERIF infrastructure entity is the superclass of all CERIF data model entities used to express infrastructures of the research activities.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF infrastructure entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#SemanticLayer">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:comment xml:lang="en">CERIF semantic layer is the superclass of all CERIF data model entities used to express the semantic layer of CERIF.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF semantic layer</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#IndicatorAndMeasurementEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:label xml:lang="en">CERIF indicator and measurement entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#AdditionalEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:label xml:lang="en">CERIF additional entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#SecondOrderEntity">
				<rdfs:subClassOf rdf:resource="https://w3id.org/cerif/model#Entity"/>
				<rdfs:label xml:lang="en">CERIF second order entity</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:Class rdf:about="https://w3id.org/cerif/model#Relationship">
				<rdfs:comment xml:lang="en">CERIF relationship is the superclass of all CERIF data model relationships.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF relationship</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:Class>
			<owl:AnnotationProperty rdf:about="http://www.w3.org/2003/06/sw-vocab-status/ns#term_status"/>
			<owl:AnnotationProperty rdf:about="https://w3id.org/cerif/model#physicalName">
				<rdfs:comment xml:lang="en">Physical name of the entity or attribute within the CERIF original data model.</rdfs:comment>
				<rdfs:label xml:lang="en">Physical name</rdfs:label>
				<vs:term_status>stable</vs:term_status>
				<rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
			</owl:AnnotationProperty>
			<owl:ObjectProperty rdf:about="https://w3id.org/cerif/model#objectProperty">
				<rdfs:comment xml:lang="en">Super-property of all object properties corresponding to attributer in the CERIF data model.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF object property</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:ObjectProperty>
			<owl:DatatypeProperty rdf:about="https://w3id.org/cerif/model#dataProperty">
				<rdfs:comment xml:lang="en">Super-property of all data properties corresponding to attributer in the CERIF data model.</rdfs:comment>
				<rdfs:label xml:lang="en">CERIF data property</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:DatatypeProperty>
			<owl:DatatypeProperty rdf:about="https://w3id.org/cerif/model#multilingualProperty">
				<rdfs:subPropertyOf rdf:resource="https://w3id.org/cerif/model#dataProperty"/>
				<rdfs:comment xml:lang="en">Identifies a property as being part of the multilingual mechanism of CERIF. The ERM data model represent multilingual attributes using a specific entity for the attributes.</rdfs:comment>
				<rdfs:label xml:lang="en">Multilingual property</rdfs:label>
				<vs:term_status>stable</vs:term_status>
			</owl:DatatypeProperty>
			
			<!--
			********************
			entities
			********************
			-->
			<xsl:for-each select="PERModelUN/Entities/PEREntityUN">
				<xsl:if test="Category/Id!='{34011F15-8D84-4858-989F-A71490A9AEEF}' or Category=''"><!--not multilingual entities, but with non-categorised entities-->
					<owl:Class>
						<xsl:attribute name="rdf:about">
							<xsl:text>https://w3id.org/cerif/model#</xsl:text>
							<xsl:value-of select="Caption"/>
						</xsl:attribute>
						<rdfs:subClassOf>
							<xsl:attribute name="rdf:resource">
								<xsl:choose>
									<xsl:when test="Category/Id='{59FA2E25-4C00-4131-92BD-AD1C87BB867C}'"><!--base entities-->
										<xsl:text>https://w3id.org/cerif/model#BaseEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{2902E5CF-9AE3-41BF-A043-D7D7CA99510A}'"><!--result entities-->
										<xsl:text>https://w3id.org/cerif/model#ResultEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{4E67698C-3316-441A-8F81-B60767BF5578}'"><!--infrastructure entities-->
										<xsl:text>https://w3id.org/cerif/model#InfrastructureEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{B854C3AE-270E-4FDD-A110-6494AE64C67A}'"><!--semantic layer-->
										<xsl:text>https://w3id.org/cerif/model#SemanticLayer</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{3494420D-7B32-4815-83DE-0229602DA0B3}'"><!--indicators and measurements-->
										<xsl:text>https://w3id.org/cerif/model#IndicatorAndMeasurementEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{A05CAB00-B5C7-46DF-9DDE-E8E82DDE46C6}'"><!--additional entities-->
										<xsl:text>https://w3id.org/cerif/model#AdditionalEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{69F7EEBC-C27B-4B02-B2DE-8E2B645669B2}'"><!--second order entities-->
										<xsl:text>https://w3id.org/cerif/model#SecondOrderEntity</xsl:text>
									</xsl:when>
									<xsl:when test="Category/Id='{AF5CAC09-E1DB-49E1-98B4-E5677B7324EF}'"><!--linking entities-->
										<xsl:text>https://w3id.org/cerif/model#Relationship</xsl:text>
									</xsl:when>
									<xsl:when test="Category=''"><!--non-categorised entities-->
										<xsl:text>https://w3id.org/cerif/model#AdditionalEntity</xsl:text>
									</xsl:when>
								</xsl:choose> 
							</xsl:attribute>
						</rdfs:subClassOf>
						<xsl:if test="Comments!=''">
							<rdfs:comment xml:lang="en"><xsl:value-of select="Comments"/></rdfs:comment>
						</xsl:if>
						<vs:term_status>
							<xsl:choose>
								<xsl:when test="contains(Comments,'deprecated')"><!--deprecated attributes-->
									<xsl:text>archaic</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>stable</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</vs:term_status>
						<rdfs:label><xsl:value-of select="Caption"/></rdfs:label>
						<physicalName><xsl:value-of select="Name"/></physicalName>
					</owl:Class>
				</xsl:if>
			</xsl:for-each>

			<!--
			********************
			properties
			********************
			-->
			<!--multilingual properties-->
			<xsl:for-each select="PERModelUN/Entities/PEREntityUN[Category/Id='{34011F15-8D84-4858-989F-A71490A9AEEF}']/Attributes/PERAttributeUN[KeyConstraintItems='']">
				<xsl:if test="contains(Name,'Src')=false"><!--remove *Src attributes; no other attributes contains Src-->
					<xsl:variable name="fkId" select="preceding-sibling::PERAttributeUN[KeyConstraintItems!='' and Name!='cfLangCode' and Name!='cfTrans']/FKForeignKeys/Id"/><!--get the id of the FK pointing to the source entity-->
					<xsl:variable name="sourceEntity" select="//PERModelUN/Entities/PEREntityUN[Keys/PERKeyConstraintUN/KeyItems/PERKeyConstraintItemUN/ForeignKeys/Id=$fkId]/Caption"/><!--get the caption of the source entity-->
					<owl:DatatypeProperty rdf:about="https://w3id.org/cerif/model#cfResultPublication.cfVersionInfo">
						<xsl:attribute name="rdf:about">
							<xsl:text>https://w3id.org/cerif/model#</xsl:text>
							<xsl:value-of select="$sourceEntity"/>
							<xsl:text>.</xsl:text>
							<xsl:value-of select="Caption"/>
						</xsl:attribute>
						<rdfs:subPropertyOf rdf:resource="https://w3id.org/cerif/model#multilingualProperty"/>
						<rdfs:domain>
							<xsl:attribute name="rdf:resource">
								<xsl:text>https://w3id.org/cerif/model#</xsl:text>
								<xsl:value-of select="$sourceEntity"/>
							</xsl:attribute>
						</rdfs:domain>
						<rdfs:range rdf:resource="http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"/>
						<xsl:if test="Comments!=''">
							<rdfs:comment xml:lang="en"><xsl:value-of select="Comments"/></rdfs:comment>
						</xsl:if>
						<vs:term_status>
							<xsl:choose>
								<xsl:when test="contains(Comments,'deprecated')"><!--deprecated attributes-->
									<xsl:text>archaic</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>stable</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</vs:term_status>
						<rdfs:label><xsl:value-of select="Caption"/></rdfs:label>
						<physicalName><xsl:value-of select="Name"/></physicalName>
					</owl:DatatypeProperty>
				</xsl:if>
			</xsl:for-each>
			
			<!--other properties (all entities except multilingual entities, including non-categorised entities)-->
			<xsl:for-each select="PERModelUN/Entities/PEREntityUN[Category/Id!='{34011F15-8D84-4858-989F-A71490A9AEEF}' or Category='']/Attributes/PERAttributeUN">
				<xsl:variable name="entity" select="ancestor::PEREntityUN/Caption"/><!--get the caption of the entity-->
				<xsl:variable name="fkId" select="FKForeignKeys/Id"/><!--get the id of the FK pointing-->
				<xsl:variable name="sourceEntity" select="//PERModelUN/Entities/PEREntityUN[Keys/PERKeyConstraintUN/KeyItems/PERKeyConstraintItemUN/ForeignKeys/Id=$fkId]/Caption"/><!--get the caption of the source entity-->
				<xsl:choose>
					<xsl:when test="FKForeignKeys=''"><!--data properties-->
						<owl:DatatypeProperty>
							<xsl:attribute name="rdf:about">
								<xsl:text>https://w3id.org/cerif/model#</xsl:text>
								<xsl:value-of select="$entity"/>
								<xsl:text>.</xsl:text>
								<xsl:value-of select="Caption"/>
							</xsl:attribute>
							<rdfs:subPropertyOf rdf:resource="https://w3id.org/cerif/model#dataProperty"/>
							<rdfs:domain>
								<xsl:attribute name="rdf:resource">
									<xsl:text>https://w3id.org/cerif/model#</xsl:text>
									<xsl:value-of select="$entity"/>
								</xsl:attribute>
							</rdfs:domain>
							<rdfs:range>
								<xsl:attribute name="rdf:resource">
									<xsl:choose>
										<xsl:when test="DataType/Id='{0288B16B-51DF-4E85-B416-0E162E38E673}' or DataType/Id='{E1E7EE23-FA33-4A67-90BB-C9B89A6133C2}' or DataType/Id='{EBD19B81-97F9-4517-8EF4-6F873A8493E4}' or DataType/Id='{ED2D5731-891A-4F3A-A861-D32D201207F3}'"><!--Blob, NClob, Char, Varchar2-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#string</xsl:text>
										</xsl:when>
										<xsl:when test="DataType/Id='{20527F83-7930-4FCA-8E7F-DD760918B229}'"><!--Date-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#dateTime</xsl:text>
										</xsl:when>
										<xsl:when test="DataType/Id='{F5C8F60C-D45C-4127-A3C2-714C2CF05CB8}'"><!--TimeStamp-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#dateTimeStamp</xsl:text>
										</xsl:when>
										<xsl:when test="DataType/Id='{1C612319-41CB-49C9-AC62-AA931826140E}' or DataType/Id='{F6C8C5A7-C12B-4ECD-9D08-66501411C34E}'"><!--Number, Integer-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#integer</xsl:text>
										</xsl:when>
										<xsl:when test="DataType/Id='{6869AC86-DFA9-4E90-9706-1CA1A28E0656}'"><!--Float-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#double</xsl:text>
										</xsl:when>
										<xsl:otherwise><!--identifiers have empty datatypes-->
											<xsl:text>http://www.w3.org/2001/XMLSchema#ID</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</rdfs:range>
							<xsl:if test="Comments!=''">
								<rdfs:comment xml:lang="en"><xsl:value-of select="Comments"/></rdfs:comment>
							</xsl:if>
							<vs:term_status>
								<xsl:choose>
									<xsl:when test="contains(Comments,'deprecated')"><!--deprecated attributes-->
										<xsl:text>archaic</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>stable</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</vs:term_status>
							<rdfs:label><xsl:value-of select="Caption"/></rdfs:label>
							<physicalName><xsl:value-of select="Name"/></physicalName>
						</owl:DatatypeProperty>
					</xsl:when>
					<xsl:when test="contains(Name,'cfClassSchemeId')=false or $sourceEntity!='cfClassification'"><!--object properties, do not consider cfClassSchemeId* FK pointing to cfClass as part of its PK-->
						<owl:ObjectProperty>
							<xsl:attribute name="rdf:about">
								<xsl:text>https://w3id.org/cerif/model#</xsl:text>
								<xsl:value-of select="$entity"/>
								<xsl:text>.</xsl:text>
								<xsl:value-of select="Caption"/>
							</xsl:attribute>
							<rdfs:subPropertyOf rdf:resource="https://w3id.org/cerif/model#objectProperty"/>
							<rdfs:domain>
								<xsl:attribute name="rdf:resource">
									<xsl:text>https://w3id.org/cerif/model#</xsl:text>
									<xsl:value-of select="$entity"/>
								</xsl:attribute>
							</rdfs:domain>
							<rdfs:range>
								<xsl:attribute name="rdf:resource">
									<xsl:text>https://w3id.org/cerif/model#</xsl:text>
									<xsl:value-of select="$sourceEntity"/>
								</xsl:attribute>
							</rdfs:range>
							<xsl:if test="Comments!=''">
								<rdfs:comment xml:lang="en"><xsl:value-of select="Comments"/></rdfs:comment>
							</xsl:if>
							<vs:term_status>
								<xsl:choose>
									<xsl:when test="contains(Comments,'deprecated')"><!--deprecated attributes-->
										<xsl:text>archaic</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>stable</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</vs:term_status>
							<rdfs:label><xsl:value-of select="Caption"/></rdfs:label>
							<physicalName><xsl:value-of select="Name"/></physicalName>
						</owl:ObjectProperty>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>

		</rdf:RDF>
	</xsl:template>
</xsl:stylesheet> 