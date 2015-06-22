<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="urn:enonic:xp:portal:1.0"
                exclude-result-prefixes="portal">

  <xsl:output method="xml" omit-xml-declaration="no"/>

  <xsl:template match="/">
    <rss version="2.0">
      <channel>
        <title>Fruits from Wikipedia</title>
        <link>
          <xsl:value-of select="portal:componentUrl()"/>
        </link>
        <description>Shows selected fruit information from Wikipedia.</description>

        <xsl:apply-templates select="/root/fruits/item"/>
      </channel>
    </rss>
  </xsl:template>

  <xsl:template match="/root/fruits/item">
    <item>
      <title>
        <xsl:value-of select="name"/>
      </title>
      <link>
        <xsl:value-of select="link"/>
      </link>
      <description>
        <xsl:value-of select="description"/>
      </description>
    </item>
  </xsl:template>

</xsl:stylesheet>
