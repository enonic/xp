package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.form.InvalidValueException;

import static org.junit.Assert.*;

public class RadioButtonsTypeTest
    extends BaseInputTypeTest
{
    public RadioButtonsTypeTest()
    {
        super( RadioButtonsType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "RadioButtons", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "RadioButtons", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "one", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test(expected = InvalidValueException.class)
    public void testValidate_invalid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "unknown" ), config );
    }

    @Test(expected = InvalidTypeException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().
            property( "option.value", "one", "two" ).
            property( "option.label", "Value One", "Value Two" ).
            build();
    }
}
