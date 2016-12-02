<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cf="urn:xmlns:org:eurocris:cerif-1.6.1-3">

	<xsl:output method="text" encoding="UTF-8"/>

	<xsl:template match="/">
<xsl:text>package org.eurocris.cerif.model;

import java.util.UUID;

public enum CERIFEntityType {
	
</xsl:text>
<xsl:apply-templates select="cf:CERIF/cf:cfClassScheme/cf:cfClass" mode="enum"/>
<xsl:text>	;

	/**
	 * Get the UUID that identifies the type.
	 */
	public abstract UUID getUUID();
	
</xsl:text>
<xsl:apply-templates select="cf:CERIF/cf:cfClassScheme/cf:cfClass" mode="uuid"/>
<xsl:text>
}
</xsl:text>
	</xsl:template>
	
	<xsl:template match="cf:cfClass" mode="enum">
		<xsl:variable name="N" select="replace( replace( replace( upper-case( cf:cfTerm ), ' ', '_' ), '2ND', 'SECOND' ), '&amp;', 'AND' )"/>
<xsl:text>	</xsl:text><xsl:value-of select="$N"/><xsl:text> {
		public UUID getUUID() {
			return </xsl:text><xsl:value-of select="$N"/><xsl:text>_UUID;
		}
	},
	
</xsl:text>
	</xsl:template>

	<xsl:template match="cf:cfClass" mode="uuid">
		<xsl:variable name="N" select="replace( replace( replace( upper-case( cf:cfTerm ), ' ', '_' ), '2ND', 'SECOND' ), '&amp;', 'AND' )"/>
<xsl:text>	private static final UUID </xsl:text><xsl:value-of select="$N"/><xsl:text>_UUID = UUID.fromString( "</xsl:text><xsl:value-of select="cf:cfClassId"/><xsl:text>" );
</xsl:text>
	</xsl:template>
</xsl:stylesheet>