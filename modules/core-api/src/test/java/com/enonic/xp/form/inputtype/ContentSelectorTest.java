package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;
import static org.junit.Assert.*;

public class ContentSelectorTest
{
    private final static ApplicationKey CURRENT_MODULE = ApplicationKey.from( "mymodule" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private ContentSelector serializer = new ContentSelector();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
        jsonHelper = new JsonTestHelper( this );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed =
            (ContentSelectorConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed = (ContentSelectorConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml(
            "parseFullConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>system:reference</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorConfig parsed =
            (ContentSelectorConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        ContentSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorConfig parsed =
            (ContentSelectorConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );
        ContentSelectorConfig expected = ContentSelectorConfig.create().build();

        // exercise
        ContentSelectorConfig parsed =
            (ContentSelectorConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }


    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeFullConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_no_relationShipType()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeEmptyConfig.json" ), json );
    }
}
