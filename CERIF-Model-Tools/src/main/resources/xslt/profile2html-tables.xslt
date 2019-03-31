<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:cf="urn:xmlns:org.eurocris.cerif" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:param name="doc-lang" select="'en'"/>
	
	<xsl:output method="html" indent="yes" />
	
	<xsl:variable name="main-namespace" select="/xs:schema/@targetNamespace"/>
	
	<xsl:key name="type-by-name" match="//xs:complexType[@name] | //xs:simpleType[@name]" use="@name"/>
	<xsl:key name="global-element-by-name" match="/xs:schema/xs:element" use="string( @name )"/>
	<xsl:key name="global-replacement-element-by-name" match="/xs:schema/xs:element" use="cf:substitutions(.)"/>
	<xsl:key name="global-group-by-name" match="/xs:schema/xs:group" use="@name"/>
	<xsl:key name="global-attribute-by-name" match="/xs:schema/xs:attribute" use="@name"/>
	<xsl:key name="global-attributeGroup-by-name" match="/xs:schema/xs:attributeGroup" use="@name"/>
	<xsl:key name="import-by-namespace" match="/xs:schema/xs:import" use="@namespace"/>
	
	<xsl:template match="/xs:schema">
<html>
<head>
<title><xsl:value-of select="substring-before( ( xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ] )[1], '&#10;' )"/></title>
<style>
table#doc-table, table.class-scheme {
	border-collapse: collapse;
}
#doc-table td, #doc-table th {
	border: 1px solid grey;
}
#doc-table tr.global-element-start {
	border-top: 2px solid black;
}
</style>
</head>
<body>
<h1><xsl:value-of select="substring-before( ( xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ] )[1], '&#10;' )"/></h1>
<table id="doc-table">
<thead>
<tr>
<th>XPath</th>
<th>Documentation</th>
<th>Populate from</th>
</tr>
</thead>
<tbody>
<xsl:apply-templates>
	<xsl:with-param name="xpath-prefix" select="''"/>
</xsl:apply-templates>
</tbody>
</table>
</body>
</html>
	</xsl:template>
	
	<xsl:template match="xs:element[ parent::xs:schema[ @targetNamespace = $main-namespace ] ]" priority="0.8">
		<xsl:param name="xpath-prefix" as="xs:string"/>
		<xsl:if test="not( @abstract )">
<tr class="global-element-start">
<td><h2 id="{@name}"><xsl:value-of select="@name"/></h2></td>
<td><xsl:value-of select="xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ]"/></td>
<td></td>
</tr>
<xsl:apply-templates mode="attributes">
	<xsl:with-param name="xpath-prefix" select="concat( @name, '/' )"/>
</xsl:apply-templates>
<xsl:apply-templates mode="elements">
	<xsl:with-param name="xpath-prefix" select="concat( @name, '/' )"/>
</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xs:element[ @name ]" mode="elements">
		<xsl:param name="xpath-prefix" as="xs:string"/>
<tr>
<td><xsl:value-of select="$xpath-prefix"/><xsl:value-of select="@name"/></td>
<td><xsl:value-of select="xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ]"/></td>
<td></td>
</tr>
<xsl:call-template name="roam">
	<xsl:with-param name="xpath-prefix" select="$xpath-prefix"/>
</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="xs:element[ @ref ]" mode="elements">
		<xsl:param name="xpath-prefix" as="xs:string"/>
		<xsl:variable name="ref" select="@ref"/>
		<xsl:variable name="refQName" select="resolve-QName( @ref, . )"/>
<tr>
<td><xsl:choose>
<xsl:when test="ends-with( @ref, '__SubstitutionGroupHead' )">
	<xsl:for-each select="key( 'global-replacement-element-by-name', $ref )[ not( @abstract ) ]">
		<xsl:value-of select="$xpath-prefix" />
		<a href="#{@name}"><xsl:value-of select="local-name-from-QName( resolve-QName( @name, . ) )" /></a>
		<xsl:if test="position() &lt; last()">
			<i> or </i><br/>
		</xsl:if>
	</xsl:for-each>
</xsl:when>
<xsl:otherwise>
	<xsl:value-of select="$xpath-prefix" />
	<xsl:choose>
		<xsl:when test="namespace-uri-from-QName( $refQName ) != $main-namespace">
			<i>x</i>:<xsl:value-of select="local-name-from-QName( $refQName )" />
		</xsl:when>
		<xsl:otherwise>
			<a href="#{@ref}"><xsl:value-of select="local-name-from-QName( $refQName )" /></a>
		</xsl:otherwise>
	</xsl:choose>
</xsl:otherwise>
</xsl:choose></td>
<td><xsl:if test="namespace-uri-from-QName( $refQName ) != $main-namespace">(Where <i>x</i> stands for the namespace <xsl:value-of select="namespace-uri-from-QName( $refQName )"/>)<br/></xsl:if>
<xsl:value-of select="xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ]"/></td>
<td></td>
</tr>
<xsl:if test="namespace-uri-from-QName( $refQName ) != $main-namespace">
<tr>
<td></td>
<td colspan="2">
<table class="class-scheme">
<thead>
<tr>
<th>Term</th>
<th>Code/URL</th>
<th>Definition</th>
<th>Mapped from</th>
</tr>
</thead>
<tbody>
		<xsl:variable name="sch1" select="document( key( 'import-by-namespace', namespace-uri-from-QName( $refQName ) )/@schemaLocation )/xs:schema"/>
		<xsl:apply-templates select="$sch1/xs:simpleType/xs:restriction/xs:enumeration[ not( xs:annotation/xs:appinfo/cf:Class/cf:Broader/cf:Class/@id ) ]" mode="vocabulary">
			<xsl:with-param name="enumNodes" select="$sch1/xs:simpleType/xs:restriction/xs:enumeration"/>
			<xsl:with-param name="indent" select="0"/>
		</xsl:apply-templates>
</tbody>
</table>
</td>
</tr>
</xsl:if>
	</xsl:template>
	
	<xsl:template match="xs:enumeration" mode="vocabulary">
		<xsl:param name="enumNodes"/>
		<xsl:param name="indent" as="xs:integer"/>
		<xsl:variable name="thisValue" select="@value"/>
<tr>
<td><xsl:for-each select="( 1 to $indent )"><xsl:text>  </xsl:text></xsl:for-each><xsl:value-of select="normalize-space( xs:annotation/xs:documentation[@xml:lang='en'] )"/></td>
<td><a href="{$thisValue}" target="_blank"><xsl:value-of select="$thisValue"/></a></td>
<td><xsl:value-of select="normalize-space( xs:annotation/xs:appinfo/cf:Class/cf:Definition[@xml:lang='en'] )"/></td>
		<xsl:variable name="children" select="$enumNodes[ xs:annotation/xs:appinfo/cf:Class/cf:Broader/cf:Class/@id = $thisValue ]"/>
<td></td>
</tr>
		<xsl:variable name="children" select="$enumNodes[ xs:annotation/xs:appinfo/cf:Class/cf:Broader/cf:Class/@id = $thisValue ]"/>
		<xsl:apply-templates select="$children" mode="#current">
			<xsl:with-param name="enumNodes" select="$enumNodes"/>
			<xsl:with-param name="indent" select="$indent + 1"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:function name="cf:substitutions">
		<xsl:param name="element"/>
		<xsl:if test="$element">
			<xsl:sequence select="( $element/@name, cf:substitutions( key( 'global-element-by-name', string( $element/@substitutionGroup ), $element/ancestor::xs:schema ) ) )"/>
		</xsl:if>
	</xsl:function>
	
	<xsl:template match="xs:attribute" mode="attributes">
		<xsl:param name="xpath-prefix" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="@name">
<tr>
<td><xsl:value-of select="$xpath-prefix"/>@<xsl:if test="namespace-uri-from-QName( resolve-QName( @ref, . ) )"><i>x</i>:</xsl:if><xsl:value-of select="@name"/></td>
<td><xsl:if test="namespace-uri-from-QName( resolve-QName( @ref, . ) )">(Where <i>x</i> stands for the namespace <xsl:value-of select="namespace-uri-from-QName( resolve-QName( @ref, . ) )"/>)<br/></xsl:if>
<xsl:choose>
<xsl:when test="@name = 'id'">Internal identifier of the <xsl:value-of select="lower-case( substring-before( $xpath-prefix, '/' ) )"/></xsl:when>
<xsl:otherwise><xsl:value-of select="xs:annotation/xs:documentation[ ancestor-or-self::*/@xml:lang[1] = $doc-lang ]"/></xsl:otherwise>
</xsl:choose></td>
<td></td>
</tr>
			</xsl:when>
			<xsl:when test="@ref">
				<xsl:apply-templates select="key( 'global-attribute-by-name', @ref )">
					<xsl:with-param name="xpath-prefix" select="$xpath-prefix"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise><xsl:message>An attribute without a @name or @ref?</xsl:message></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	<xsl:template match="xs:annotation" mode="#all"/>
	
	<xsl:template name="roam">
		<xsl:param name="xpath-prefix"/>
		<xsl:variable name="new-xpath-prefix" select="concat( $xpath-prefix, @name, @ref, '/' )"/>
		<xsl:apply-templates select="key( 'type-by-name', @type )" mode="#current">
			<xsl:with-param name="xpath-prefix" select="$new-xpath-prefix"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="xs:complexType" mode="#current">
			<xsl:with-param name="xpath-prefix" select="$new-xpath-prefix"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xs:extension" mode="#all">
		<xsl:param name="xpath-prefix"/>
		<xsl:apply-templates select="key( 'type-by-name', @base )" mode="#current">
			<xsl:with-param name="xpath-prefix" select="$xpath-prefix"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="#current">
			<xsl:with-param name="xpath-prefix" select="$xpath-prefix"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xs:group[@ref]" mode="#all">
		<xsl:param name="xpath-prefix"/>
		<xsl:apply-templates select="key( 'global-group-by-name', @ref )" mode="#current">
			<xsl:with-param name="xpath-prefix" select="$xpath-prefix"/>
		</xsl:apply-templates>
	</xsl:template>
	
</xsl:stylesheet>