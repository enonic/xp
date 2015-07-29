package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;
import static org.junit.Assert.*;

public class FileUploaderConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private FileUploaderConfigJsonSerializer serializer = new FileUploaderConfigJsonSerializer();

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
        FileUploaderConfig config = FileUploaderConfig.create().
            allowType( "Images", "jpg,png,gif" ).
            allowType( "Text", "txt,doc" ).
            hideDropZone( true ).
            build();

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
        FileUploaderConfig expected = FileUploaderConfig.create().
            allowType( "Images", "jpg,png,gif" ).
            allowType( "Text", "txt,doc" ).
            hideDropZone( true ).
            build();

        // exercise
        FileUploaderConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected, parsed );
    }

    @Test
    public void parseConfig_allowTypes_not_existing()
        throws IOException
    {
        // setup
        String json = "{}";
        FileUploaderConfig expected = FileUploaderConfig.create().build();

        // exercise
        FileUploaderConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json ) );

        // verify
        assertEquals( expected, parsed );
    }
}
