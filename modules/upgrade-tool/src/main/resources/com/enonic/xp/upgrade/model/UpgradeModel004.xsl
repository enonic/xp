<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template
      match="/node/data/property-set[@name='attachment' and string[@name='label'] ='small']">
  </xsl:template>
  <xsl:template
      match="/node/data/property-set[@name='attachment' and string[@name='label'] ='medium']">
  </xsl:template>
  <xsl:template
      match="/node/data/property-set[@name='attachment' and string[@name='label'] ='large']">
  </xsl:template>
  <xsl:template
      match="/node/data/property-set[@name='attachment' and string[@name='label'] ='extra-large']">
  </xsl:template>

  <!--Identity template,
          provides default behavior that copies all content into the output -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>