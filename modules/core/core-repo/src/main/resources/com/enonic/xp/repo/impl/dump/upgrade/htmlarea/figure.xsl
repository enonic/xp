<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nl "&#xa;">
    ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xhtml" indent="no"/>

  <xsl:template match="/">
    <xsl:apply-templates select="/root/*"/>
    <xsl:text>&#xa;&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:left')]">
    <figure class="editor-align-left" style='float: left; width: 40%;'>
      <xsl:text>&#xa;</xsl:text>
      <xsl:apply-templates select="*"/>
      <xsl:text>&#xa;</xsl:text>
    </figure>
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:right')]">
    <figure class="editor-align-right" style='float: right; width: 40%;'>
      <xsl:text>&#xa;</xsl:text>
      <xsl:apply-templates select="*"/>
      <xsl:text>&#xa;</xsl:text>
    </figure>
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:none')]">
    <figure class="editor-align-center" style='margin: auto; width: 60%;'>
      <xsl:text>&#xa;</xsl:text>
      <xsl:apply-templates select="*"/>
      <xsl:text>&#xa;</xsl:text>
    </figure>
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="figure[@class='justify']">
    <figure class="editor-align-justify">
      <xsl:text>&#xa;</xsl:text>
      <xsl:apply-templates select="*"/>
      <xsl:text>&#xa;</xsl:text>
    </figure>
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>