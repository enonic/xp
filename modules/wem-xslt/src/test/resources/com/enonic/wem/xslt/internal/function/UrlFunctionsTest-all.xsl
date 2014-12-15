<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/xp/portal">

  <xsl:output method="xml"/>

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:url('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:url('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:pageUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:pageUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:imageUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:imageUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:assetUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:assetUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:attachmentUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:attachmentUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:serviceUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:serviceUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:componentUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:componentUrl('a=1','b=2')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
