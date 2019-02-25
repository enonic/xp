<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nl "&#xa;">
    ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xhtml" indent="no"/>

  <!-- Process the document based on the incoming XHTML document root element -->
  <xsl:template match="/">
    <xsl:apply-templates select="/root/*"/>
  </xsl:template>

  <!--Copies and processes all elements and attributes-->
  <xsl:template match="/root/*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
    <xsl:text>&#xa;&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
  </xsl:template>

  <!--<xsl:template match="figure[@style='float:left; margin:15px; width:40%']">-->
    <!--<figure class="editor-align-left" style='float: left; width: 40%;'>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
      <!--<xsl:apply-templates select="*"/>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
    <!--</figure>-->
    <!--<xsl:text>&#xa;&#xa;</xsl:text>-->
  <!--</xsl:template>-->

  <!--<xsl:template match="figure[@style='float:right; margin:15px; width:40%']">-->
    <!--<figure class="editor-align-right" style='float: right; width: 40%;'>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
      <!--<xsl:apply-templates select="*"/>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
    <!--</figure>-->
    <!--<xsl:text>&#xa;&#xa;</xsl:text>-->
  <!--</xsl:template>-->

  <!--<xsl:template match="figure[@style='float:none; margin:auto; width:60%']">-->
    <!--<figure class="editor-align-center" style='margin: auto; width: 60%;'>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
      <!--<xsl:apply-templates select="*"/>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
    <!--</figure>-->
    <!--<xsl:text>&#xa;&#xa;</xsl:text>-->
  <!--</xsl:template>-->

  <!--<xsl:template match="figure[@class='justify']">-->
    <!--<figure class="editor-align-justify">-->
      <!--<xsl:text>&#xa;</xsl:text>-->
      <!--<xsl:apply-templates select="*"/>-->
      <!--<xsl:text>&#xa;</xsl:text>-->
    <!--</figure>-->
    <!--<xsl:text>&#xa;&#xa;</xsl:text>-->
  <!--</xsl:template>-->

</xsl:stylesheet>