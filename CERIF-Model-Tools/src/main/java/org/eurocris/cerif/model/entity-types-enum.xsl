<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cf="urn:xmlns:org:eurocris:cerif-1.6.1-3">

	<xsl:output method="text" encoding="UTF-8"/>

	<xsl:template match="/">
		<xsl:for-each select="cf:CERIF/cf:cfClassScheme">
<xsl:text>package org.eurocris.cerif.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eurocris.cerif.CERIFClassScheme;

/**
 * Enumeration: </xsl:text><xsl:value-of select="cf:cfName"/><xsl:text>.
 */
@CERIFClassScheme( id="</xsl:text><xsl:value-of select="cf:cfClassSchemeId"/><xsl:text>", name="</xsl:text><xsl:value-of select="cf:cfName"/><xsl:text>" )
public enum CERIFEntityType {
	
</xsl:text>
<xsl:apply-templates select="cf:cfClass" mode="enum"/>
<xsl:text>	;

	/**
	 * Get the UUID that identifies the cfClass.
	 */
	public abstract UUID getUuid();
	
	/**
	 * Get the English term for the cfClass.
	 */
	public abstract String getTerm();
	
	private final static Map&lt;UUID, CERIFEntityType&gt; VALUE_BY_UUID = new HashMap&lt;&gt;();
	static {
		for ( final CERIFEntityType x : values() ) {
			VALUE_BY_UUID.put( x.getUuid(), x );
		}
	}
	
	/**
	 * Get the right entry by its UUID.
	 * @param uuid
	 * @return
	 */
	public static CERIFEntityType getByUuid( final UUID uuid ) {
		return VALUE_BY_UUID.get( uuid );
	}
	
</xsl:text>
<xsl:apply-templates select="cf:cfClass" mode="uuid"/>
<xsl:text>
}
</xsl:text>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="cf:cfClass" mode="enum">
		<xsl:variable name="N" select="replace( replace( replace( upper-case( cf:cfTerm ), ' ', '_' ), '2ND', 'SECOND' ), '&amp;', 'AND' )"/>
<xsl:text>	</xsl:text><xsl:value-of select="$N"/><xsl:text> {
		public UUID getUuid() {
			return </xsl:text><xsl:value-of select="$N"/><xsl:text>_UUID;
		}
		public String getTerm() {
			return "</xsl:text><xsl:value-of select="cf:cfTerm"/><xsl:text>";
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