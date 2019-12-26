package com.enonic.xp.testing.helper;

import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class JsonAssert
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    public static void assertJson( final Class context, final String name, final MapSerializable value )
        throws Exception
    {
        final String resource = "/" + context.getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = context.getResource( resource );

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
