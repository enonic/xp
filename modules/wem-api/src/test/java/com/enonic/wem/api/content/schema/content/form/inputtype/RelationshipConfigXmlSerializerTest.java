package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.io.IOException;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.enonic.wem.api.XmlTestHelper;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static junit.framework.Assert.assertEquals;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class RelationshipConfigXmlSerializerTest
{
    private XmlTestHelper xmlHelper;

    private RelationshipConfigXmlSerializer serializer = new RelationshipConfigXmlSerializer();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException, SAXException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig config = builder.build();

        // exercise
        Element configEl = new Element( "config" );
        serializer.generateConfig( config, configEl );

        // verify
        assertXMLEqual( xmlHelper.loadTestFile( "serializeConfig.xml" ), xmlHelper.serialize( configEl, true ) );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig expected = builder.build();

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( xmlHelper.loadXml( "parseConfig.xml" ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }


    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>System:like</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }


    @Test
    public void parseConfig_with_contentTypeFilter_not_existing()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type>System:like</relationship-type>\n" );
        xml.append( "</config>\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }


    @Test
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter><allow>System:audio</allow><allow>System:image</allow></content-type-filter>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter><allow>System:audio</allow><allow>System:image</allow></content-type-filter>\n" );
        xml.append( "</config>\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( null, parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }
}
