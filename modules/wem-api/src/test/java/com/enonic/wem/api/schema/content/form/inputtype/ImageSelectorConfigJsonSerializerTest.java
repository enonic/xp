package com.enonic.wem.api.schema.content.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class ImageSelectorConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private ImageSelectorConfigJsonSerializer serializer = new ImageSelectorConfigJsonSerializer();

    @Before
    public void before()
    {
        jsonHelper = new JsonTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        ImageSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test(expected = NullPointerException.class)
    public void serializeConfig_with_no_relationShipType()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        ImageSelectorConfig config = builder.build();

        // exercise
        serializer.serializeConfig( config, jsonHelper.objectMapper() );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        ImageSelectorConfig expected = builder.build();

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowContentTypes\": [\"audio\", \"image\"]\n" );
        json.append( "}\n" );

        // exercise
        serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );
    }
}
