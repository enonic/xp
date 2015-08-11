package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class ImageSelectorTypeTest
{
    private final static ApplicationKey CURRENT_MODULE = ApplicationKey.from( "mymodule" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private ImageSelectorType serializer = new ImageSelectorType();

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
        ImageSelectorTypeConfig.Builder builder = ImageSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorTypeConfig expected = builder.build();

        // exercise
        ImageSelectorTypeConfig parsed = (ImageSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml(
            "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        ImageSelectorTypeConfig.Builder builder = ImageSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>system:reference</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ImageSelectorTypeConfig parsed =
            (ImageSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        ImageSelectorTypeConfig.Builder builder = ImageSelectorTypeConfig.create();
        ImageSelectorTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ImageSelectorTypeConfig parsed =
            (ImageSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_not_specified()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );
        ImageSelectorTypeConfig expected = ImageSelectorTypeConfig.create().build();

        // exercise
        ImageSelectorTypeConfig parsed =
            (ImageSelectorTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        ImageSelectorTypeConfig.Builder builder = ImageSelectorTypeConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorTypeConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        this.jsonHelper.assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_no_explicit_relationShipType()
        throws IOException
    {
        // setup
        ImageSelectorTypeConfig.Builder builder = ImageSelectorTypeConfig.create();
        ImageSelectorTypeConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        this.jsonHelper.assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
