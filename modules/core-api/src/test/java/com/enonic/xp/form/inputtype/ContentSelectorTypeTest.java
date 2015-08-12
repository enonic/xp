package com.enonic.xp.form.inputtype;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

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

    private InputTypeConfig parseXml( final String name )
    {
        return this.serializer.parseConfig( CURRENT_APPLICATION, xmlHelper.parseXml( name ).getDocumentElement() );
    }

    @Test
    public void parseConfig()
    {
        final InputTypeConfig parsed = parseXml( "parseConfig.xml" );
        assertEquals( RelationshipTypeName.REFERENCE.toString(), parsed.getValue( "relationshipType" ) );
        assertTrue( Strings.isNullOrEmpty( parsed.getValue( "allowedContentTypes" ) ) );
    }

    @Test
    public void parseFullConfig()
    {
        final InputTypeConfig parsed = parseXml( "parseFullConfig.xml" );
        assertEquals( RelationshipTypeName.REFERENCE.toString(), parsed.getValue( "relationshipType" ) );
        assertEquals( ContentTypeName.videoMedia().toString() + "," + ContentTypeName.imageMedia().toString(),
                      parsed.getValue( "allowedContentTypes" ) );
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
