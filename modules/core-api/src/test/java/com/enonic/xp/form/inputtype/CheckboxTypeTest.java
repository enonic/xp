package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class CheckboxTypeTest
    extends BaseInputTypeTest
{
    public CheckboxTypeTest()
    {
        super( CheckboxType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Checkbox", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Checkbox", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "true", config );

        assertNotNull( value );
        assertSame( ValueTypes.BOOLEAN, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( stringProperty( "value" ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( booleanProperty( true ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, stringProperty( "value" ) );
    }
}
