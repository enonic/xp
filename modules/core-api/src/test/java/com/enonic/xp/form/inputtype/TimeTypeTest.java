package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class TimeTypeTest
    extends BaseInputTypeTest
{
    public TimeTypeTest()
    {
        super( TimeType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Time", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Time", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "22:11:00", config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
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
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, localTimeProperty() );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckValidity_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, booleanProperty( true ) );
    }
}
