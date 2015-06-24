package com.enonic.xp.testing.script;

import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;

public final class ScriptAssert
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.enable( SerializationFeature.INDENT_OUTPUT );
        MAPPER.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        MAPPER.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    public static void assertJson( final Object expected, final Object actual )
        throws Exception
    {
        final Object expectedObj = JsObjectConverter.fromJs( expected );
        final Object actualObj = JsObjectConverter.fromJs( actual );

        final String expectedJson = MAPPER.writeValueAsString( expectedObj );
        final String actualJson = MAPPER.writeValueAsString( actualObj );

        Assert.assertEquals( expectedJson, actualJson );
    }
}
