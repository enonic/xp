package com.enonic.xp.portal.impl.macro;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class MacroContextMapperTest
{
    private MacroContext macroContext;

    private final ObjectMapper mapper;

    public MacroContextMapperTest()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    private void assertJson( final String name, final MapSerializable value )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = this.mapper.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = this.mapper.writeValueAsString( expectedJson );
        final String actualStr = this.mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }

    @Before
    public void setup()
    {
        this.macroContext = MacroContext.create().
            name( "macroName" ).
            body( "body" ).
            param( "firstParam", "firstParamValue" ).
            param( "secondParam", "secondParamValue" ).
            build();
    }

    @Test
    public void testMapping()
        throws Exception
    {
        assertJson( "mapping", new MacroContextMapper( this.macroContext ) );
    }
}
