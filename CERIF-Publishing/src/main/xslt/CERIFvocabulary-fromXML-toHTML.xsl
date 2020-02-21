<?xml version="1.0" encoding="UTF-8"?>
<!--
This stylesheet is used to transform a CERIF XML vocabulary file to a CERIF HTML vocabulary
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="urn:xmlns:org:eurocris:cerif-1.5-1" exclude-result-prefixes="x">
	<xsl:output method="html" encoding="utf-8" indent="yes" />

	<xsl:template match="/">
		<xsl:for-each select="x:CERIF/x:cfClassScheme">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>	
		<html>
			<head>
				<meta charset="utf-8"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
				<title>[CERIF] <xsl:value-of select="x:cfName"/> vocabulary</title>
				<link rel="stylesheet" href="css/cerif-bulma.css"/>
				<script src="https://use.fontawesome.com/releases/v5.3.1/js/all.js" defer="true"></script>
			</head>
			<body>
			
				<div class="header has-background-primary">
					<div class="site-branding has-text-light">
						<div class="site-name-slogan">        
							<h2 class="site-name has-text-primary"><a href="https://www.eurocris.org" title="euroCRIS website" class="has-text-light">euroCRIS</a></h2>
							<h6 class="site-slogan">Current Research Information Systems</h6>
						</div>
						<div class="site-title">
							The International Organisation for Research Information
						</div>
					</div>
				</div>
				
				<section class="section">

					<xsl:variable name="schemeShortName" select="x:cleanLabelForURL(x:cfName)"/>
					<xsl:variable name="schemeName" select="x:cfName"/>
	
					<!--class scheme-->
					<article class="container">
						<h1 class="title is-1">
							<xsl:value-of select="x:cfName"/> vocabulary
						</h1>
						<p class="subtitle">
							<a href="#">https://w3id.org/cerif/vocab/<xsl:value-of select="$schemeShortName"/></a>
						</p>
						<div class="tile is-ancestor">
							<div class="tile is-2 has-text-light has-background-primary">
								rdf:type
							</div>
							<div class="tile">
								<ul>
									<li><a href="https://w3id.org/cerif/model#cfClassificationScheme" target="_blank">https://w3id.org/cerif/model#cfClassificationScheme</a></li>
									<li><a href="http://www.w3.org/2004/02/skos/core#ConceptScheme" target="_blank">http://www.w3.org/2004/02/skos/core#ConceptScheme</a></li>
								</ul>
							</div>
						</div>
						<div class="tile is-ancestor">
							<div class="tile is-2 has-text-light has-background-primary">
									dc:identifier
							</div>
							<div class="tile">
								<ul>
									<li><xsl:value-of select="x:cfClassSchemeId"/></li>
								</ul>
							</div>
						</div>
						<div class="tile is-ancestor">
							<div class="tile is-2 has-text-light has-background-primary">
									rdfs:label
							</div>
							<div class="tile">
								<ul>
									<li>(<xsl:value-of select="x:cfName/@cfLangCode"/>) <xsl:value-of select="x:cfName"/></li>
								</ul>
							</div>
						</div>
						<div class="tile is-ancestor">
							<div class="tile is-2 has-text-light has-background-primary">
									dc:description
							</div>
							<div class="tile">
								<ul>
									<li>(<xsl:value-of select="x:cfDescr/@cfLangCode"/>) <xsl:value-of select="x:cfDescr"/></li>
								</ul>
							</div>
						</div>
						<div class="tile is-ancestor">
							<div class="tile is-2 has-text-light has-background-primary">
									skos:hasTopConcept
							</div>
							<div class="tile">
								<ul>
									<xsl:for-each select="x:cfClass">
										<li>
											<a>
												<xsl:attribute name="href">
													<xsl:text>#</xsl:text>
													<xsl:value-of select="x:cleanLabelForURL(x:cfTerm)"/>
												</xsl:attribute>
												<xsl:value-of select="x:cfTerm"/>
											</a>
										</li>
									</xsl:for-each>
								</ul>
							</div>
						</div>
					</article>
			
					<!--classes-->
					<xsl:for-each select="x:cfClass">
						<article class="container">
							<xsl:variable name="classShortName" select="x:cleanLabelForURL(x:cfTerm)"/>
							<xsl:attribute name="id">
								<xsl:value-of select="$classShortName"/>
							</xsl:attribute>
							<h3 class="title">
								<xsl:value-of select="x:cfTerm"/>
							</h3>
							<p class="subtitle">
								<a>
									<xsl:attribute name="href">
										<xsl:text>#</xsl:text>
										<xsl:value-of select="$classShortName"/>
									</xsl:attribute>
									https://w3id.org/cerif/vocab/<xsl:value-of select="$schemeShortName"/>#<xsl:value-of select="$classShortName"/>
								</a>
							</p>
							<div class="tile is-ancestor">
								<div class="tile is-2 has-text-light has-background-primary">
										rdf:type
								</div>
								<div class="tile">
									<ul>
										<li><a href="https://w3id.org/cerif/model#cfClassification" target="_blank">https://w3id.org/cerif/model#cfClassification</a></li>
										<li><a href="http://www.w3.org/2004/02/skos/core#Concept" target="_blank">http://www.w3.org/2004/02/skos/core#Concept</a></li>
									</ul>
								</div>
							</div>
							<div class="tile is-ancestor">
								<div class="tile is-2 has-text-light has-background-primary">
										skos:topConceptOf
								</div>
								<div class="tile">
									<ul>
										<li><a href="#"><xsl:value-of select="$schemeName"/> vocabulary</a></li>
									</ul>
								</div>
							</div>
							<div class="tile is-ancestor">
								<div class="tile is-2 has-text-light has-background-primary">
										dc:identifier
								</div>
								<div class="tile">
									<ul>
										<li><xsl:value-of select="x:cfClassId"/></li>
									</ul>
								</div>
							</div>
							<div class="tile is-ancestor">
								<div class="tile is-2 has-text-light has-background-primary">
										rdfs:label
								</div>
								<div class="tile">
									<ul>
										<li>(<xsl:value-of select="x:cfTerm/@cfLangCode"/>) <xsl:value-of select="x:cfTerm"/></li>
									</ul>
								</div>
							</div>
							<xsl:if test="x:cfDescr">
								<div class="tile is-ancestor">
									<div class="tile is-2 has-text-light has-background-primary">
											dc:description
									</div>
									<div class="tile">
										<ul>
											<li>(<xsl:value-of select="x:cfDescr/@cfLangCode"/>) <xsl:value-of select="x:cfDescr"/></li>
										</ul>
									</div>
								</div>
							</xsl:if>
							<xsl:if test="x:cfDef">
								<div class="tile is-ancestor">
									<div class="tile is-2 has-text-light has-background-primary">
											skos:definition
									</div>
									<div class="tile">
										<ul>
											<li>(<xsl:value-of select="x:cfDef/@cfLangCode"/>) <xsl:value-of select="x:cfDef"/></li>
										</ul>
									</div>
								</div>
							</xsl:if>
							<xsl:if test="x:cfTermSrc | x:cfDescrSrc | x:cfDefSrc">
								<div class="tile is-ancestor">
									<div class="tile is-2 has-text-light has-background-primary">
											dc:source
									</div>
									<div class="tile">
										<ul>
											<xsl:for-each select="x:cfTermSrc | x:cfDescrSrc | x:cfDefSrc">
												<li>(<xsl:value-of select="@cfLangCode"/>) <xsl:value-of select="text()"/></li>
											</xsl:for-each>
										</ul>
									</div>
								</div>
							</xsl:if>
						</article>							
					</xsl:for-each>

				</section>
			</body>
		</html>
		</xsl:for-each>
	</xsl:template>

	<xsl:function name="x:cleanLabelForURL">
		<xsl:param name="originalValue"/>
		<xsl:value-of select="translate(translate($originalValue,' ',''),'-','')"/>
	</xsl:function>
</xsl:stylesheet>