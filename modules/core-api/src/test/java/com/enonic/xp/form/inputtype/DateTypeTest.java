package com.enonic.xp.form.inputtype;


import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class DateTypeTest
    extends BaseInputTypeTest
{
    public DateTypeTest()
    {
        super( DateType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Date", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Date", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = newEmptyConfig();
        final Value value = this.type.createPropertyValue( "2015-01-02", config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( localDateProperty() );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( referenceProperty( "name" ) );
    }

    @Test(expected = InputValidationException.class)
    public void testContract_invalid()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.checkValidity( config, stringProperty( "name" ) );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
