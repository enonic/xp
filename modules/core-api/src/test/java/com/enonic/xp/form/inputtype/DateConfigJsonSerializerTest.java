package com.enonic.xp.form.inputtype;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;

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
        DateConfig.Builder builder = DateConfig.create();
        builder.withTimezone( true );
        DateConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
