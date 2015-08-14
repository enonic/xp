package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class DoubleTypeTest
    extends BaseInputTypeTest
{
    public DoubleTypeTest()
    {
        super( DoubleType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Double", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Double", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "1.3", config );

        assertNotNull( value );
        assertSame( ValueTypes.DOUBLE, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( doubleProperty( 1.3 ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
