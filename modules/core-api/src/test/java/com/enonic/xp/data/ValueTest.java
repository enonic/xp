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
        assertEquals( "abc", Value.newString( "abc" ).toString() );
        assertEquals( "<div>abc</div>", Value.newHtmlPart( "<div>abc</div>" ).toString() );
        assertEquals( "<xml></xml>", Value.newXml( "<xml></xml>" ).toString() );
        assertEquals( "false", Value.newBoolean( false ).toString() );
        assertEquals( "abc", Value.newReference( Reference.from( "abc" ) ).toString() );
        assertEquals( "1.1,-1.1", Value.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) ).toString() );
        assertEquals( "1.1", Value.newDouble( 1.1 ).toString() );
        assertEquals( "1", Value.newLong( 1L ).toString() );
        assertEquals( "2012-01-01", Value.newLocalDate( LocalDate.of( 2012, 1, 1 ) ).toString() );
        assertEquals( "2012-01-01T12:00:00", Value.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) ).toString() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tostring_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet mySet = tree.addSet( "mySet" );
        mySet.addStrings( "strings", "a", "b", "c" );
        tree.getValue( "mySet" ).toString();
    }

    @Test
    public void checkValueType()
    {
        assertTrue( Value.newString( "string" ).isString() );
        assertTrue( Value.newLocalDate( LocalDate.now() ).isDateType() );
        assertTrue( Value.newLong( 1L ).isNumericType() );
        assertTrue( Value.newGeoPoint( GeoPoint.from( "20,20" ) ).isGeoPoint() );
        assertTrue( Value.newDouble( 2.0 ).isJavaType( Double.class ) );
        assertEquals( ValueTypes.STRING, Value.newString( "string" ).getType() );

    }

    @Test
    public void check_conversion_returns_null_when_supposed_to()
    {
        Value value = Value.newString( null );
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
        Value value = Value.newString( "asda" );
        value.asLong();

    }

    @Test
    public void copy()
    {
        Value stringValue = Value.newString( "string" );
        Value binaryReferenceValue = Value.newBinary( BinaryReference.from( "binary" ) );
        Value booleanValue = Value.newBoolean( true );
        Value dataValue = Value.newData( new PropertySet() );
        Value doubleValue = Value.newDouble( 2.0 );
        Value geoPointValue = Value.newGeoPoint( GeoPoint.from( "20,20" ) );
        Value instantValue = Value.newInstant( Instant.now() );
        Value linkValue = Value.newLink( Link.from( "link" ) );
        Value localDateValue = Value.newLocalDate( LocalDate.now() );
        Value localDateTimeValue = Value.newLocalDateTime( LocalDateTime.now() );
        Value localTImeValue = Value.newLocalTime( LocalTime.now() );
        Value referenceValue = Value.newReference( Reference.from( "ref" ) );

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