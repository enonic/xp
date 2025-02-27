package com.enonic.xp.testing.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class JsonAssert
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    public static void assertMapper( final Class<?> context, String fileName, final MapSerializable value )
    {
        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();
        final JsonNode expectedNode = readFromFile( context, fileName );
        assertEquals( expectedNode, actualJson );
    }

    public static void assertJson( final Class<?> context, final String name, final MapSerializable value )
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

    private static JsonNode readFromFile( final Class<?> context, final String fileName )
    {
        final InputStream stream =
            Objects.requireNonNull( context.getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            return MAPPER.readTree( stream.readAllBytes() );
        } catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read file [" + fileName + "]", e );
        }
    }
}
