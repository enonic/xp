package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

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
    @Ignore
    public void given_data_that_validates_checkValidity_throws_nothing()
        throws InvalidValueTypeException, InvalidValueException
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor.red", new Value.WholeNumber( 40 ) );
        contentData.setProperty( "myColor.green", new Value.WholeNumber( 40 ) );
        contentData.setProperty( "myColor.blue", new Value.WholeNumber( 40 ) );

        Property myColor = contentData.getProperty( "myColor" );

        InputTypes.COLOR.checkValidity( myColor );
    }

    @Test
    @Ignore
    public void given_data_missing_red_checkValidity_throws_InvalidDataException()
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor.green", new Value.WholeNumber( 40l ) );
        contentData.setProperty( "myColor.blue", new Value.WholeNumber( 40l ) );

        Property myColor = contentData.getProperty( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Excpected exception" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof InvalidDataException );
            assertEquals(
                "Invalid data [Data{path=myColor, type=Set, value=myColor { green, blue }}]: data required to have sub data at path: red",
                e.getMessage() );
        }
    }

    @Test
    @Ignore
    public void given_data_with_illegal_red_checkValidity_throws_InvalidDataException()
    {
        Content content = newContent().build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "myColor.red", new Value.WholeNumber( 256 ) );
        contentData.setProperty( "myColor.green", new Value.WholeNumber( 40 ) );
        contentData.setProperty( "myColor.blue", new Value.WholeNumber( 40 ) );

        Property myColor = contentData.getProperty( "myColor" );

        try
        {
            InputTypes.COLOR.checkValidity( myColor );
            fail( "Excpected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof InvalidValueException );
            assertEquals(
                "Invalid value in [Data{path=myColor.red, type=WholeNumber, value=256}]: Value not within range from 0 to 255: 256",
                e.getMessage() );
        }
    }
}
