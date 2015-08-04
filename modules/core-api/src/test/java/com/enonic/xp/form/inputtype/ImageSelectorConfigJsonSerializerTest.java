package com.enonic.xp.form.inputtype;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.JsonTestHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;

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
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_no_explicit_relationShipType()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.create();
        ImageSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
