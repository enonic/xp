package com.enonic.wem.core.content.datatype;

import org.junit.Test;

import com.enonic.wem.core.content.Content;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;

import static org.junit.Assert.*;


public class GeographicCoordinateTest
{
    @Test(expected = InvalidValueTypeException.class)
    public void given_data_with_value_as_text_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setData( "myGeographicCoordinate.latitude", "1.1" );

        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = content.getData( "myGeographicCoordinate" );

        // exercise
        geographicCoordinate.checkValidity( data );
    }

    @Test
    public void given_data_with_value_of_correct_type_but_illegal_value_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // setup
        Content content = new Content();
        content.setData( "myGeographicCoordinate.latitude", 91.0 );
        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = content.getData( "myGeographicCoordinate" );

        // exercise
        try
        {
            geographicCoordinate.checkValidity( data );
            fail( "Expected InvalidValueException" );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected InvalidValueException", e instanceof InvalidValueException );
            assertEquals( "A latitude is ranging from -90 to +90: 91.0", e.getMessage() );
        }
    }
}
