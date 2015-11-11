package com.enonic.xp.testing.json;

import java.net.URL;

import org.junit.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class JsonAssert
{
    public static void assertJson( final Class context, final String name, final MapSerializable value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();

        final String resource = "/" + context.getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = context.getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = mapper.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = mapper.writeValueAsString( expectedJson );
        final String actualStr = mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }
}
