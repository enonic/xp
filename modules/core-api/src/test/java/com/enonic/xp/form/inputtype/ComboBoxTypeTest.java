package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class ComboBoxTypeTest
    extends BaseInputTypeTest
{
    public ComboBoxTypeTest()
    {
        super( ComboBoxType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ComboBox", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ComboBox", this.type.toString() );
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

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidValue()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "unknown" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = newValidConfig();
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
