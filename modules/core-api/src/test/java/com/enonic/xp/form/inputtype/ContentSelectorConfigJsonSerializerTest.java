package com.enonic.xp.form.inputtype;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.JsonTestHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;

public class ContentSelectorConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private ContentSelectorConfigJsonSerializer serializer = new ContentSelectorConfigJsonSerializer();

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
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

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
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

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
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeEmptyConfig.json" ), json );
    }
}
