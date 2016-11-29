package com.enonic.xp.lib.node;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.node.mapper.PropertyTreeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.serializer.JsonMapGenerator;

import static org.junit.Assert.*;

public class PropertyMapperTest
    extends BaseNodeHandlerTest
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
        throws Exception
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    @Test
    public void object()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "object" );
        assertEquals( new Long( 1 ), properties.getLong( "a" ) );
        assertEquals( new Long( 2 ), properties.getLong( "b" ) );
    }

    @Test
    public void doubleTest()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "double" );

        assertNotNull( properties.getDouble( "myDouble" ) );
    }

    @Test
    public void array()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "array" );
        assertNotNull( properties.getStrings( "myArray" ) );
    }

    @Test
    public void permissions()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "permissions" );
        assertJson( "permissions-result", properties );
    }

    @Test
    public void indexConfig()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "indexConfig" );
        assertJson( "indexConfig-result", properties );
    }

    @Test
    public void full()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "full" );
        assertJson( "full-result", properties );
    }

    private void assertJson( final String name, final PropertyTree value )
        throws Exception
    {
        final String resource = name + ".json";
        final URL url = getClass().getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = this.mapper.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        new PropertyTreeMapper( value ).serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = this.mapper.writeValueAsString( expectedJson );
        final String actualStr = this.mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }

    private PropertyTree getPropertyTree( final String name )
    {
        return getCreateNodeHandlerParams( name ).getPropertyTree();
    }

    private ScriptValueTranslatorResult getCreateNodeHandlerParams( final String name )
    {
        final ScriptExports exports = runScript( "/com/enonic/xp/lib/node/property-mapper.js" );
        final ScriptValue value = exports.executeMethod( name );

        return new ScriptValueTranslator().create( value );
    }
}
