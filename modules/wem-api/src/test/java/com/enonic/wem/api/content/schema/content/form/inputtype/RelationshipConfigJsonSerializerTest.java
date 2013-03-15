package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class RelationshipConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private RelationshipConfigJsonSerializer serializer = new RelationshipConfigJsonSerializer();

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
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig config = builder.build();

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
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig expected = builder.build();

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }

    @Test
    public void parseConfig_with_allowedContentTypes_as_empty()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowedContentTypes\": [],\n" );
        json.append( "\"relationshipType\": \"System:like\"\n" );
        json.append( "}\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }

    @Test
    public void parseConfig_with_allowedContentTypes_as_null()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowedContentTypes\": null,\n" );
        json.append( "\"relationshipType\": \"System:like\"\n" );
        json.append( "}\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }

    @Test
    public void parseConfig_with_allowedContentTypes_not_existing()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"relationshipType\": \"System:like\"\n" );
        json.append( "}\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }

    @Test
    public void parseConfig_relationshipType_as_null()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.allowedContentType( QualifiedContentTypeName.audioFile() );
        builder.allowedContentType( QualifiedContentTypeName.imageFile() );
        RelationshipConfig expected = builder.build();

        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowedContentTypes\": [\"System:audio\", \"System:image\"],\n" );
        json.append( "\"relationshipType\": null\n" );
        json.append( "}\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );

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

        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowedContentTypes\": [\"System:audio\", \"System:image\"]\n" );
        json.append( "}\n" );

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );

        // verify
        assertEquals( null, parsed.getRelationshipType() );
        assertEquals( expected.getAllowedContentTypes(), parsed.getAllowedContentTypes() );
    }
}
