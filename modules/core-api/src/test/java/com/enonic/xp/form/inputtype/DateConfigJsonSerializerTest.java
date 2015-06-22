package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;
import static org.junit.Assert.*;

public class DateConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private DateConfigJsonSerializer serializer = new DateConfigJsonSerializer();

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
        DateConfig.Builder builder = DateConfig.newDateConfig();
        builder.withTimezone( true );
        DateConfig config = builder.build();

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
        DateConfig.Builder builder = DateConfig.newDateConfig();
        builder.withTimezone( true );
        DateConfig expected = builder.build();

        // exercise
        DateConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        String json = "{}";
        DateConfig expected = DateConfig.newDateConfig().build();

        // exercise
        DateConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json ) );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }
}
