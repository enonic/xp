package com.enonic.wem.api.content.data.type;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;

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
        rootDataSet.setData( "myGeographicCoordinate", new Value.DecimalNumber( 1.1 ) );

        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = rootDataSet.getData( "myGeographicCoordinate" );

        // exercise
        geographicCoordinate.checkValidity( data );
    }

    @Test
    public void given_data_with_value_of_correct_type_but_illegal_value_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // exercise
        RootDataSet rootDataSet = new RootDataSet();

        // exercise
        try
        {
            rootDataSet.setData( "myGeographicCoordinate", new Value.GeographicCoordinate( "90.0,180.2" ) );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( "Expected InvalidDataException, got: " + e.getClass().getSimpleName(), e instanceof InvalidDataException );
            assertEquals( "Invalid data: Data{name=myGeographicCoordinate, type=GeographicCoordinate, value=90.0,180.2}", e.getMessage() );
        }
    }


}
