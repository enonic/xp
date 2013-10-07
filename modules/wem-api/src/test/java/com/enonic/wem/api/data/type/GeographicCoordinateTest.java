package com.enonic.wem.api.data.type;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

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
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myGeographicCoordinate", new Value.Double( 1.1 ) );

        GeographicCoordinate geographicCoordinate = ValueTypes.GEOGRAPHIC_COORDINATE;
        Property property = contentData.getProperty( "myGeographicCoordinate" );

        // exercise
        geographicCoordinate.checkValidity( property );
    }

    @Test
    public void given_data_with_value_of_correct_type_but_illegal_value_then_checkValidity_throws_InvalidValueTypeException()
        throws Exception
    {
        // exercise
        ContentData contentData = new ContentData();

        // exercise
        try
        {
            contentData.setProperty( "myGeographicCoordinate", new Value.GeographicCoordinate( "90.0,180.2" ) );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( "Expected InvalidValueException, got: " + e.getClass().getSimpleName(), e instanceof InvalidValueException );
            assertEquals( "Invalid value: longitude not within range from -180.0 to 180.0: 90.0,180.2", e.getMessage() );
        }
    }


}
