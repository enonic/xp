package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.form.InvalidValueException;

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
        final Value value = this.type.createPropertyValue( "one", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( stringProperty( "value" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( booleanProperty( true ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.checkValidity( config, stringProperty( "one" ) );
    }

    @Test(expected = InvalidValueException.class)
    public void testCheckValidity_invalid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.checkValidity( config, stringProperty( "unknown" ) );
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().
            property( "option.value", "one", "two" ).
            property( "option.label", "Value One", "Value Two" ).
            build();
    }
}
