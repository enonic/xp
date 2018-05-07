package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class MediaSelectorTypeTest
    extends BaseInputTypeTest
{
    public MediaSelectorTypeTest()
    {
        super( MediaSelectorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "MediaSelector", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "MediaSelector", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "name" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( referenceProperty( "name" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( booleanProperty( true ), config );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
