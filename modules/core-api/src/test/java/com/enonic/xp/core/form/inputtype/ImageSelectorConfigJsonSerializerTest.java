package com.enonic.xp.core.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.core.form.inputtype.ImageSelectorConfig;
import com.enonic.xp.core.form.inputtype.ImageSelectorConfigJsonSerializer;
import com.enonic.xp.core.schema.relationship.RelationshipTypeName;
import com.enonic.xp.core.support.JsonTestHelper;

import static com.enonic.xp.core.support.JsonTestHelper.assertJsonEquals;
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
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        ImageSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorConfig expected = builder.build();

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        String json = "{}";
        ImageSelectorConfig expected = ImageSelectorConfig.newImageSelectorConfig().build();

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }
}
