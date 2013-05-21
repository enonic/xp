package com.enonic.wem.api.schema.content.form.inputtype;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class ImageConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private ImageConfigJsonSerializer serializer = new ImageConfigJsonSerializer();

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
        ImageConfig.Builder builder = ImageConfig.newImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        ImageConfig config = builder.build();

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
        ImageConfig.Builder builder = ImageConfig.newImageConfig();
        ImageConfig config = builder.build();

        // exercise
        serializer.serializeConfig( config, jsonHelper.objectMapper() );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ImageConfig.Builder builder = ImageConfig.newImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        ImageConfig expected = builder.build();

        // exercise
        ImageConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

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
        json.append( "\"allowContentTypes\": [\"system:audio\", \"system:image\"]\n" );
        json.append( "}\n" );

        // exercise
        serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );
    }
}
