package com.enonic.xp.lib.node;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.lib.node.mapper.PropertyTreeMapper;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.serializer.JsonMapGenerator;

import static org.junit.Assert.*;

public class ScriptValueTranslatorTest
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
    public void geoPoint()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "geoPoint" );
        assertNotNull( properties.getGeoPoint( "myGeoPoint" ) );
        validateType( properties, "myGeoPoint", ValueTypes.GEO_POINT );
    }

    @Test
    public void instant()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "instant" );
        assertNotNull( properties.getInstant( "myInstant" ) );
        validateType( properties, "myInstant", ValueTypes.DATE_TIME );
    }

    @Test
    public void booleanTest()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "boolean" );
        assertNotNull( properties.getBoolean( "myBoolean" ) );
        validateType( properties, "myBoolean", ValueTypes.BOOLEAN );
    }

    @Test
    public void reference()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "reference" );
        assertNotNull( properties.getReference( "myReference" ) );
        validateType( properties, "myReference", ValueTypes.REFERENCE );
    }

    @Test
    public void localDateTime()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "localDateTime" );
        assertNotNull( properties.getLocalDateTime( "myLocalDateTime" ) );
        validateType( properties, "myLocalDateTime", ValueTypes.LOCAL_DATE_TIME );
    }

    @Test
    public void localDate()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "localDate" );
        assertNotNull( properties.getLocalDate( "myLocalDate" ) );
        validateType( properties, "myLocalDate", ValueTypes.LOCAL_DATE );
    }

    @Test
    public void localTime()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "localTime" );
        assertNotNull( properties.getLocalTime( "myLocalTime" ) );
        validateType( properties, "myLocalTime", ValueTypes.LOCAL_TIME );

    }

    @Test
    public void doubleTest()
        throws Exception
    {
        final PropertyTree properties = getPropertyTree( "double" );

        assertNotNull( properties.getDouble( "myDouble" ) );
        validateType( properties, "myDouble", ValueTypes.DOUBLE );
    }

    private void validateType( final PropertyTree properties, final String propertyName, final ValueType valueType )
    {
        final Property prop = properties.getProperty( propertyName );
        assertEquals( valueType, prop.getType() );
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
    public void binary()
        throws Exception
    {
        final ScriptValueTranslatorResult params = getCreateNodeHandlerParams( "binary" );
        final PropertyTree properties = params.getPropertyTree();

        assertNotNull( properties.getBinaryReference( "myBinary" ) );
        validateType( properties, "myBinary", ValueTypes.BINARY_REFERENCE );

        final BinaryAttachments binaryAttachments = params.getBinaryAttachments();
        assertEquals( 1, binaryAttachments.getSize() );
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
        final ScriptExports exports = runScript( "/com/enonic/xp/lib/node/script-values.js" );
        final ScriptValue value = exports.executeMethod( name );

        return new ScriptValueTranslator().create( value );
    }

}