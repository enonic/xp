package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ValueTest
{
    @Test
    public void tostring()
    {
        assertEquals( "abc", ValueFactory.newString( "abc" ).toString() );
        assertEquals( "<xml></xml>", ValueFactory.newXml( "<xml></xml>" ).toString() );
        assertEquals( "false", ValueFactory.newBoolean( false ).toString() );
        assertEquals( "abc", ValueFactory.newReference( Reference.from( "abc" ) ).toString() );
        assertEquals( "1.1,-1.1", ValueFactory.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) ).toString() );
        assertEquals( "1.1", ValueFactory.newDouble( 1.1 ).toString() );
        assertEquals( "1", ValueFactory.newLong( 1L ).toString() );
        assertEquals( "2012-01-01", ValueFactory.newLocalDate( LocalDate.of( 2012, 1, 1 ) ).toString() );
        assertEquals( "2012-01-01T12:00:00", ValueFactory.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) ).toString() );
    }

    @Test
    public void tostring_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );
        tree.addSet( "myEmptySet" );
        PropertySet mySet = tree.addSet( "mySet" );
        mySet.addStrings( "strings", "a", "b", "c" );

        assertEquals( "\n", tree.getValue( "myEmptySet" ).toString() );
        assertEquals( "\n        strings: [a, b, c]", tree.getValue( "mySet" ).toString() );
    }

    @Test
    public void checkValueType()
    {
        assertTrue( ValueFactory.newString( "string" ).isString() );
        assertTrue( ValueFactory.newLocalDate( LocalDate.now() ).isDateType() );
        assertTrue( ValueFactory.newLong( 1L ).isNumericType() );
        assertTrue( ValueFactory.newGeoPoint( GeoPoint.from( "20,20" ) ).isGeoPoint() );
        assertTrue( ValueFactory.newDouble( 2.0 ).isJavaType( Double.class ) );
        assertSame( ValueTypes.STRING, ValueFactory.newString( "string" ).getType() );

    }

    @Test
    public void check_conversion_returns_null_when_supposed_to()
    {
        Value value = ValueFactory.newString( null );
        assertNull( value.asLong() );
        assertNull( value.asBinaryReference() );
        assertNull( value.asBoolean() );
        assertNull( value.asData() );
        assertNull( value.asDouble() );
        assertNull( value.asGeoPoint() );
        assertNull( value.asInstant() );
        assertNull( value.asLink() );
        assertNull( value.asLocalDate() );
        assertNull( value.asLocalDateTime() );
        assertNull( value.asLocalTime() );
        assertNull( value.asReference() );
        assertNull( value.asString() );
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void check_conversion_throws_exception_when_supposed_to()
    {
        thrown.expect( ValueTypeException.class );
        Value value = ValueFactory.newString( "asda" );
        value.asLong();

    }

    @Test
    public void copy()
    {
        Value stringValue = ValueFactory.newString( "string" );
        Value binaryReferenceValue = ValueFactory.newBinaryReference( BinaryReference.from( "binary" ) );
        Value booleanValue = ValueFactory.newBoolean( true );
        Value dataValue = ValueFactory.newPropertySet( new PropertySet() );
        Value doubleValue = ValueFactory.newDouble( 2.0 );
        Value geoPointValue = ValueFactory.newGeoPoint( GeoPoint.from( "20,20" ) );
        Value instantValue = ValueFactory.newDateTime( Instant.now() );
        Value linkValue = ValueFactory.newLink( Link.from( "link" ) );
        Value localDateValue = ValueFactory.newLocalDate( LocalDate.now() );
        Value localDateTimeValue = ValueFactory.newLocalDateTime( LocalDateTime.now() );
        Value localTImeValue = ValueFactory.newLocalTime( LocalTime.now() );
        Value referenceValue = ValueFactory.newReference( Reference.from( "ref" ) );

        assertEquals( stringValue, stringValue.copy( null ) );
        assertEquals( binaryReferenceValue, binaryReferenceValue.copy( null ) );
        assertEquals( booleanValue, booleanValue.copy( null ) );
        assertEquals( dataValue, dataValue.copy( null ) );
        assertEquals( doubleValue, doubleValue.copy( null ) );
        assertEquals( geoPointValue, geoPointValue.copy( null ) );
        assertEquals( instantValue, instantValue.copy( null ) );
        assertEquals( linkValue, linkValue.copy( null ) );
        assertEquals( localDateValue, localDateValue.copy( null ) );
        assertEquals( localDateTimeValue, localDateTimeValue.copy( null ) );
        assertEquals( localTImeValue, localTImeValue.copy( null ) );
        assertEquals( referenceValue, referenceValue.copy( null ) );

    }

}
