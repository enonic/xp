<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <fruits>
      <xsl:apply-templates select="//fruits/item"/>
    </fruits>
  </xsl:template>

  <xsl:template match="//fruits/item">
    <fruit name="{name}" color="{color}"/>
  </xsl:template>

</xsl:stylesheet>
