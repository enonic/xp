package com.enonic.wem.api.content.data.datatype;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;


@Ignore
public class GeographicCoordinateTest
{
    @Test(expected = InvalidDataTypeException.class)
    public void given_data_with_value_as_text_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // setup
        Content content = newContent().build();
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
        Content content = newContent().build();
        content.setData( "myGeographicCoordinate.latitude", 90.0 );
        content.setData( "myGeographicCoordinate.longitude", 180.2 );
        GeographicCoordinate geographicCoordinate = DataTypes.GEOGRAPHIC_COORDINATE;
        Data data = content.getData( "myGeographicCoordinate" );

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
                "Invalid value in [Data{path=myGeographicCoordinate.longitude, type=DecimalNumber, value=180.2}]: Value not within range from -180 to 180: 180.2",
                e.getMessage() );
        }
    }


}
