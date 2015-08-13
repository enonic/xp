package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class ImageSelectorTypeTest
    extends BaseInputTypeTest
{
    public ImageSelectorTypeTest()
    {
        super( ImageSelectorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ImageSelector", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ImageSelector", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "name", config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( referenceProperty( "name" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.checkValidity( config, referenceProperty( "value" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckValidity_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.checkValidity( config, booleanProperty( true ) );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
