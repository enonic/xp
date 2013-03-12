package com.enonic.wem.api.content.data.type;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;


public class GeographicCoordinateTest
{
    @Test
    public void getLatitude()
    {
        assertEquals( 59.913869, GeographicCoordinate.getLatitude( "59.913869,10.752245" ), 0 );
    }

    @Test
    public void getLongitude()
    {
        assertEquals( 10.752245, GeographicCoordinate.getLongitude( "59.913869,10.752245" ), 0 );
    }

    @Test(expected = InvalidValueTypeException.class)
    public void given_data_with_value_as_double_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // setup
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setData( "myGeographicCoordinate", 1.1 );

        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = rootDataSet.getData( "myGeographicCoordinate" );

        // exercise
        geographicCoordinate.checkValidity( data );
    }

    @Test
    public void given_data_with_value_of_correct_type_but_illegal_value_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // setup
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setData( "myGeographicCoordinate", "90.0,180.2" );
        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = rootDataSet.getData( "myGeographicCoordinate" );

        // exercise
        try
        {
            geographicCoordinate.checkValidity( data );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected InvalidValueException, got: " + e.getClass().getSimpleName(), e instanceof InvalidValueException );
            assertEquals(
                "Invalid value in [Data{name=myGeographicCoordinate, type=Text, value=90.0,180.2}]: Value not within range from -180.0 to 180.0: 90.0,180.2",
                e.getMessage() );
        }
    }


}
