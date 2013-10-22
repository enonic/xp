package com.enonic.wem.api.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.form.InvalidValueException;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;

public class ColorTest
{
    @Test
    public void construct()
    {
        new Color();
    }

    @Test
    public void given_data_that_validates_checkValidity_throws_nothing()
        throws InvalidValueTypeException, InvalidValueException
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor", new Value.String( "40;40;40" ) );

        Property myColor = contentData.getProperty( "myColor" );

        InputTypes.COLOR.checkValidity( myColor );
    }

    @Test
    public void given_data_missing_red_checkValidity_throws_InvalidValueException()
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor", new Value.String( ";40;40" ) );

        Property myColor = contentData.getProperty( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof InvalidValueException );
            assertEquals(
                "Invalid value in [String{name=myColor, type=String, value=;40;40}]: Integer value for color red not given: : ;40;40",
                e.getMessage() );
        }
    }

    @Test
    public void given_data_with_illegal_red_checkValidity_throws_InvalidValueException()
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor", new Value.String( "256;40;40" ) );

        Property myColor = contentData.getProperty( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof InvalidValueException );
            assertEquals(
                "Invalid value in [String{name=myColor, type=String, value=256;40;40}]: Value of color red must be between 0 and 255: 256;40;40",
                e.getMessage() );
        }
    }
}
