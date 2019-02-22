<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>

  <xsl:template match="/">
    <xsl:apply-templates select="/root/*"/>
  </xsl:template>

  <xsl:template match="figure[@style='float:left; margin:15px; width:40%']">
    <figure class="editor-align-left" style='float: left; width: 40%;'>
      <xsl:apply-templates select="*"/>
    </figure>
  </xsl:template>

  <xsl:template match="figure[@style='float:right; margin:15px; width:40%']">
    <figure class="editor-align-right" style='float: right; width: 40%;'>
      <xsl:apply-templates select="*"/>
    </figure>
  </xsl:template>

  <xsl:template match="figure[@style='float:none; margin:auto; width:60%']">
    <figure class="editor-align-center" style='margin: auto; width: 60%;'>
      <xsl:apply-templates select="*"/>
    </figure>
  </xsl:template>

  <xsl:template match="figure[@class='justify']">
    <figure class="editor-align-justify">
      <xsl:apply-templates select="*"/>
    </figure>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy-of select="current()"/>
  </xsl:template>

</xsl:stylesheet>