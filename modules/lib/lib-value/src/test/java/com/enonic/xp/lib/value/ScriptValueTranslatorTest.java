package com.enonic.xp.lib.value;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScriptValueTranslatorTest
    extends ScriptTestSupport
{
    @Test
    void geoPoint()
    {
        final PropertyTree properties = getPropertyTree( "geoPoint" );
        assertNotNull( properties.getGeoPoint( "myGeoPoint" ) );
        validateType( properties, "myGeoPoint", ValueTypes.GEO_POINT );
    }

    @Test
    void instant()
    {
        final PropertyTree properties = getPropertyTree( "instant" );
        assertNotNull( properties.getInstant( "myInstant" ) );
        validateType( properties, "myInstant", ValueTypes.DATE_TIME );
    }

    @Test
    void instantFromDate()
    {
        final PropertyTree properties = getPropertyTree( "instantFromDate" );
        assertNotNull( properties.getInstant( "myInstant" ) );
        assertEquals( properties.getInstant( "myInstantExpected" ), properties.getInstant( "myInstant" ) );
        validateType( properties, "myInstant", ValueTypes.DATE_TIME );
    }

    @Test
    void booleanTest()
    {
        final PropertyTree properties = getPropertyTree( "boolean" );
        assertNotNull( properties.getBoolean( "myBoolean" ) );
        validateType( properties, "myBoolean", ValueTypes.BOOLEAN );
    }

    @Test
    void reference()
    {
        final PropertyTree properties = getPropertyTree( "reference" );
        assertNotNull( properties.getReference( "myReference" ) );
        validateType( properties, "myReference", ValueTypes.REFERENCE );
    }

    @Test
    void localDateTime()
    {
        final PropertyTree properties = getPropertyTree( "localDateTime" );
        assertNotNull( properties.getLocalDateTime( "myLocalDateTime" ) );
        validateType( properties, "myLocalDateTime", ValueTypes.LOCAL_DATE_TIME );
    }

    @Test
    void localDateTimeFromDate()
    {
        final PropertyTree properties = getPropertyTree( "localDateTimeFromDate" );
        assertNotNull( properties.getLocalDateTime( "myLocalDateTime" ) );
        validateType( properties, "myLocalDateTime", ValueTypes.LOCAL_DATE_TIME );
        assertEquals( properties.getLocalDateTime( "myLocalDateTimeExpected" ), properties.getLocalDateTime( "myLocalDateTime" ) );
    }

    @Test
    void localDate()
    {
        final PropertyTree properties = getPropertyTree( "localDate" );
        assertNotNull( properties.getLocalDate( "myLocalDate" ) );
        validateType( properties, "myLocalDate", ValueTypes.LOCAL_DATE );
    }

    @Test
    void localDateFromDate()
    {
        final PropertyTree properties = getPropertyTree( "localDateFromDate" );
        assertNotNull( properties.getLocalDate( "myLocalDate" ) );
        assertEquals( properties.getLocalDate( "myLocalDateExpected" ), properties.getLocalDate( "myLocalDate" ) );
        validateType( properties, "myLocalDate", ValueTypes.LOCAL_DATE );
        assertEquals( properties.getLocalDate( "myLocalDateExpected" ), properties.getLocalDate( "myLocalDate" ) );
    }

    @Test
    void localTime()
    {
        final PropertyTree properties = getPropertyTree( "localTime" );
        assertNotNull( properties.getLocalTime( "myLocalTime" ) );
        validateType( properties, "myLocalTime", ValueTypes.LOCAL_TIME );
    }

    @Test
    void localTimeFromDate()
    {
        final PropertyTree properties = getPropertyTree( "localTimeFromDate" );
        assertNotNull( properties.getLocalTime( "myLocalTime" ) );
        validateType( properties, "myLocalTime", ValueTypes.LOCAL_TIME );
        assertEquals( properties.getLocalTime( "myLocalTimeExpected" ), properties.getLocalTime( "myLocalTime" ) );
    }

    @Test
    void date()
    {
        final PropertyTree properties = getPropertyTree( "date" );
        assertNotNull( properties.getInstant( "myDate" ) );
        validateType( properties, "myDate", ValueTypes.DATE_TIME );
    }

    @Test
    void binary()
    {
        final ScriptValueTranslatorResult params = getCreateNodeHandlerParams( "binary" );
        final PropertyTree properties = params.getPropertyTree();

        assertNotNull( properties.getBinaryReference( "myBinary" ) );
        validateType( properties, "myBinary", ValueTypes.BINARY_REFERENCE );

        final BinaryAttachments binaryAttachments = params.getBinaryAttachments();
        assertEquals( 1, binaryAttachments.getSize() );
    }

    @Test
    void integer()
    {
        final PropertyTree properties = getPropertyTree( "integer" );
        assertNotNull( properties.getLong( "myInteger" ) );
        validateType( properties, "myInteger", ValueTypes.LONG );
    }

    @Test
    void byteTest()
    {
        final PropertyTree properties = getPropertyTree( "byte" );
        assertNotNull( properties.getLong( "myByte" ) );
        validateType( properties, "myByte", ValueTypes.LONG );
    }

    @Test
    void longTest()
    {
        final PropertyTree properties = getPropertyTree( "long" );
        assertNotNull( properties.getLong( "myLong" ) );
        validateType( properties, "myLong", ValueTypes.LONG );
    }

    @Test
    void doubleTest()
    {
        final PropertyTree properties = getPropertyTree( "double" );
        assertNotNull( properties.getDouble( "myDouble" ) );
        validateType( properties, "myDouble", ValueTypes.DOUBLE );
    }

    @Test
    void floatTest()
    {
        final PropertyTree properties = getPropertyTree( "float" );
        assertNotNull( properties.getDouble( "myFloat" ) );
        validateType( properties, "myFloat", ValueTypes.DOUBLE );
    }

    @Test
    void numberTest()
    {
        final PropertyTree properties = getPropertyTree( "number" );
        assertNotNull( properties.getDouble( "myNumber" ) );
        validateType( properties, "myNumber", ValueTypes.DOUBLE );
    }

    @Test
    void defaultValue()
    {
        final PropertyTree properties = getPropertyTree( "defaultValue" );
        assertNotNull( properties.getString( "myDefaultType" ) );
        assertEquals( "SUNDAY", properties.getString( "myDefaultType" ) );
        validateType( properties, "myDefaultType", ValueTypes.STRING );
    }

    @Test
    void arrayTest()
    {
        final PropertyTree properties = getPropertyTree( "array" );
        assertNotNull( properties.getLong( "myArray" ) );
        assertNotNull( properties.getLong( "myArray", 1 ) );
        assertNotNull( properties.getLong( "myArray", 2 ) );
        validateType( properties, "myArray", ValueTypes.LONG );
    }

    @Test
    void mapTest()
    {
        final PropertyTree properties = getPropertyTree( "map" );
        assertNotNull( properties.getSet( "myMap" ) );
        assertNotNull( properties.getSet( "myMap.a" ) );
        assertNotNull( properties.getLong( "myMap.a.b" ) );
        validateType( properties, "myMap", ValueTypes.PROPERTY_SET );
        validateType( properties, "myMap.a", ValueTypes.PROPERTY_SET );
        validateType( properties, "myMap.a.b", ValueTypes.LONG );
    }

    private void validateType( final PropertyTree properties, final String propertyName, final ValueType valueType )
    {
        final Property prop = properties.getProperty( propertyName );
        assertEquals( valueType, prop.getType() );
    }

    private PropertyTree getPropertyTree( final String name )
    {
        return getCreateNodeHandlerParams( name ).getPropertyTree();
    }

    private ScriptValueTranslatorResult getCreateNodeHandlerParams( final String name )
    {
        final ScriptExports exports = runScript( "/com/enonic/xp/lib/value/script-values.js" );
        final ScriptValue value = exports.executeMethod( name );

        return new ScriptValueTranslator().create( value );
    }

    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
