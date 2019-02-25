<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nl "&#xa;">
    ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xhtml" indent="no"/>

  <xsl:template match="/">
    <xsl:apply-templates select="/root/*"/>
  </xsl:template>

  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
    <xsl:call-template name="root-element-format"/>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:left')]">
    <figure class="" style='float: left; width: 40%;'>
      <xsl:call-template name="figure-common">
        <xsl:with-param name="class-value">editor-align-left</xsl:with-param>
      </xsl:call-template>
    </figure>
    <xsl:call-template name="root-element-format"/>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:right')]">
    <figure class="" style='float: right; width: 40%;'>
      <xsl:call-template name="figure-common">
        <xsl:with-param name="class-value">editor-align-right</xsl:with-param>
      </xsl:call-template>
    </figure>
    <xsl:call-template name="root-element-format"/>
  </xsl:template>

  <xsl:template match="figure[starts-with(@style,'float:none')]">
    <figure class="" style='margin: auto; width: 60%;'>
      <xsl:call-template name="figure-common">
        <xsl:with-param name="class-value">editor-align-center</xsl:with-param>
      </xsl:call-template>
    </figure>
    <xsl:call-template name="root-element-format"/>
  </xsl:template>

  <xsl:template match="figure[@class='justify']">
    <figure class="">
      <xsl:call-template name="figure-common">
        <xsl:with-param name="class-value">editor-align-justify</xsl:with-param>
      </xsl:call-template>
    </figure>
    <xsl:call-template name="root-element-format"/>
  </xsl:template>

  <xsl:template name="root-element-format">
    <xsl:if test="parent::root">
      <xsl:text>&#xa;&#xa;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template name="figure-common">
    <xsl:param name="class-value" />
    <xsl:attribute name="class"><xsl:value-of select="$class-value"/></xsl:attribute>
    <xsl:if test="child::img[starts-with(@src, 'media://')]">
      <xsl:attribute name="class"><xsl:value-of select="$class-value"/> editor-style-original</xsl:attribute>
    </xsl:if>
    <xsl:text>&#xa;</xsl:text>
    <xsl:apply-templates select="*"/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>


</xsl:stylesheet>