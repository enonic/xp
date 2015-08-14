package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

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
        final Value value = this.type.createValue( "13", config );

        assertNotNull( value );
        assertSame( ValueTypes.LONG, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( longProperty( 13 ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
