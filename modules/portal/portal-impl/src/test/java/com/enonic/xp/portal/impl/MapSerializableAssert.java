package com.enonic.xp.portal.impl;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class MapSerializableAssert
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT ).
        enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );

    private final Class<?> clazz;

    public MapSerializableAssert( Class<?> clazz )
    {
        this.clazz = clazz;
    }

    public void assertJson( final String resource, final MapSerializable value )
        throws IOException
    {
        final URL url = clazz.getResource( resource );
        assertNotNull( url, "File [" + resource + "] not found" );

        final JsonNode expectedJson = MAPPER.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = MAPPER.writeValueAsString( expectedJson );
        final String actualStr = MAPPER.writeValueAsString( actualJson );

        assertEquals( expectedStr, actualStr );
    }
}
