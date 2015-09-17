<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/node">
    <xsl:element name="node">
      <xsl:copy-of select="node()"/>

      <xsl:if test="not(/node/timestamp)">
        <xsl:element name="timestamp">

          <xsl:choose>
            <xsl:when test="/node/data/dateTime[@name='modifiedTime']">
              <xsl:value-of select="/node/data/dateTime[@name='modifiedTime']"></xsl:value-of>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="/node/data/dateTime[@name='createdTime']"></xsl:value-of>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:if>

    </xsl:element>

  </xsl:template>


  <!--Identity template,
          provides default behavior that copies all content into the output -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>