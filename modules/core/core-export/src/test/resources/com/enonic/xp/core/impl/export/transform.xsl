<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>
  <xsl:param name="applicationId"/>
  <xsl:variable name="placeholderApp" select="'com.enonic.apps.genericapp'"/>

  <xsl:template match="string[starts-with(text(),concat($placeholderApp,':'))]">
    <string>
      <xsl:attribute name="name">
        <xsl:value-of select="@name"/>
      </xsl:attribute>
      <xsl:value-of select="concat($applicationId, substring-after(.,$placeholderApp))"/>
    </string>
  </xsl:template>

  <!--Identity template,
          provides default behavior that copies all content into the output -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>