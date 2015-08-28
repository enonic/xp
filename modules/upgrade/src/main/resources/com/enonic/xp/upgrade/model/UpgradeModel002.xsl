<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/node/data/property-set[@name='x']/property-set[@name='base']/property-set/@name[.='gps-info']">
    <xsl:attribute name="name">
      <xsl:value-of select="'gpsInfo'"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set/@name[.='photo-info']">
    <xsl:attribute name="name">
      <xsl:value-of select="'cameraInfo'"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set/@name[.='image-info']">
    <xsl:attribute name="name">
      <xsl:value-of select="'imageInfo'"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template
      match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set[@name='image-info']/string[@name='imageWidth']">
    <long>
      <xsl:apply-templates select="@*|node()"/>
    </long>
  </xsl:template>

  <xsl:template
      match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set[@name='image-info']/string[@name='imageHeight']">
    <long>
      <xsl:apply-templates select="@*|node()"/>
    </long>
  </xsl:template>

  <xsl:template
      match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set[@name='image-info']/string[@name='bytesize']">
    <long>
      <xsl:apply-templates select="@*|node()"/>
    </long>
  </xsl:template>


  <xsl:template
      match="/node/data/property-set[@name='x']/property-set[@name='media']/property-set[@name='image-info']/string/@name[.='bytesize']">
    <xsl:attribute name="name">
      <xsl:value-of select="'byteSize'"/>
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