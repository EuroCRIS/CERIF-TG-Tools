<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:cf="urn:xmlns:org:eurocris:cerif-1.6-2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:param name="classSchemeType" select="'Type'" />

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<xsl:apply-templates select="//cf:cfClassScheme" />
	</xsl:template>

	<xsl:template match="cf:cfClassScheme">
		<xsl:variable name="ns" select="concat( 'http://openaire.eu/cerif/vocab/', fn:replace( cf:cfName, ' ', '' ), '#' )" />
		<xsl:result-document method="xml" indent="yes" href="{concat( fn:replace( cf:cfName, ' ', '' ), '.xsd' )}">
			<xsl:element name="xs:schema">
				<xsl:attribute name="elementFormDefault">qualified</xsl:attribute>
				<xsl:attribute name="targetNamespace" select="$ns" />
				<xsl:namespace name="" select="$ns" />
	
				<xsl:element name="xs:element">
					<xsl:attribute name="name" select="$classSchemeType" />
					<xsl:element name="xs:simpleType">
						<xsl:element name="xs:restriction">
							<xsl:attribute name="base">xs:string</xsl:attribute>
							<xsl:apply-templates select="cf:cfClass" />
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:result-document>
	</xsl:template>

	<xsl:template match="cf:cfClass">
		<xsl:element name="xs:enumeration">
			<xsl:attribute name="value" select="fn:replace( cf:cfTerm, ' ', '' )" />
			<xsl:element name="xs:annotation">
				<xsl:element name="xs:documentation">
					<xsl:value-of select="cf:cfDef" />
					<xsl:if test="cf:cfDefSrc">
						<xsl:text> [</xsl:text>
						<xsl:value-of select="cf:cfDefSrc" />
						<xsl:text>]</xsl:text>
					</xsl:if>
				</xsl:element>
				<xsl:element name="xs:appinfo">
					<xsl:attribute name="source">CERIF</xsl:attribute>
					<xsl:copy-of select="." exclude-result-prefixes="xsi"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>