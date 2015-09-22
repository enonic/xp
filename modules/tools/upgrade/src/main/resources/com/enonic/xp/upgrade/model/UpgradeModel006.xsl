<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/node/indexConfigs/pathIndexConfigs">

    <xsl:element name="pathIndexConfigs">
      <xsl:copy-of select="node()"/>

      <xsl:if test="not(./pathIndexConfig/path/text()[.='creator'])">
        <xsl:element name="pathIndexConfig">
          <indexConfig>
            <decideByType>false</decideByType>
            <enabled>true</enabled>
            <nGram>false</nGram>
            <fulltext>false</fulltext>
            <includeInAllText>false</includeInAllText>
          </indexConfig>
          <path>creator</path>
        </xsl:element>
      </xsl:if>

      <xsl:if test="not(./pathIndexConfig/path/text()[.='owner'])">
        <xsl:element name="pathIndexConfig">
          <indexConfig>
            <decideByType>false</decideByType>
            <enabled>true</enabled>
            <nGram>false</nGram>
            <fulltext>false</fulltext>
            <includeInAllText>false</includeInAllText>
          </indexConfig>
          <path>owner</path>
        </xsl:element>
      </xsl:if>

      <xsl:if test="not(./pathIndexConfig/path/text()[.='modifier'])">
        <xsl:element name="pathIndexConfig">
          <indexConfig>
            <decideByType>false</decideByType>
            <enabled>true</enabled>
            <nGram>false</nGram>
            <fulltext>false</fulltext>
            <includeInAllText>false</includeInAllText>
          </indexConfig>
          <path>modifier</path>
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