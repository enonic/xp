<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template
      match="/node/data/property-set[@name='data' and ..//string[@name='type']= 'portal:site']/property-set[@name='moduleConfig']/@name">
    <xsl:attribute name="name">
      <xsl:value-of select="'siteConfig'"/>
    </xsl:attribute>
  </xsl:template>

  <!--Identity template,
          provides default behavior that copies all content into the output -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>