package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class LongTypeTest
    extends BaseInputTypeTest
{
    public LongTypeTest()
    {
        super( LongType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Long", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Long", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "13", config );

        assertNotNull( value );
        assertSame( ValueTypes.LONG, value.getType() );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( longProperty( 13 ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, longProperty( 13 ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckValidity_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, booleanProperty( true ) );
    }
}
