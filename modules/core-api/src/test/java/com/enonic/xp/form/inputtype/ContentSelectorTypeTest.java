package com.enonic.xp.form.inputtype;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;

import static org.junit.Assert.*;

public class ContentSelectorTypeTest
{
    private final static ApplicationKey CURRENT_APPLICATION = ApplicationKey.from( "myapplication" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private ContentSelectorType serializer = new ContentSelectorType();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
        jsonHelper = new JsonTestHelper( this );
    }

    private InputTypeConfig parse( final String name )
    {
        // setup
        ContentSelectorTypeConfig.Builder builder = ContentSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorTypeConfig expected = builder.build();

        // exercise
        ContentSelectorTypeConfig parsed = (ContentSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml(
            "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig()
    {
        // setup
        ContentSelectorTypeConfig.Builder builder = ContentSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorTypeConfig expected = builder.build();

        // exercise
        ContentSelectorTypeConfig parsed = (ContentSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml(
            "parseFullConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_allowed_content_types()
    {
        // setup
        ContentSelectorTypeConfig.Builder builder = ContentSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>system:reference</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorTypeConfig parsed =
            (ContentSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        ContentSelectorTypeConfig.Builder builder = ContentSelectorTypeConfig.create();
        ContentSelectorTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorTypeConfig parsed =
            (ContentSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

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
        ContentSelectorTypeConfig expected = ContentSelectorTypeConfig.create().build();

        // exercise
        ContentSelectorTypeConfig parsed =
            (ContentSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }


    @Test
    public void serializeConfig()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).
            build();

        final JsonNode json = this.serializer.serializeConfig( config );
        this.jsonHelper.assertJsonEquals( this.jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_allowed_content_types()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).
            property( "allowedContentTypes", ContentTypeName.imageMedia().toString() + "," + ContentTypeName.videoMedia() ).
            build();

        final JsonNode json = serializer.serializeConfig( config );
        this.jsonHelper.assertJsonEquals( this.jsonHelper.loadTestJson( "serializeFullConfig.json" ), json );
    }
}
