package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

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
        final Value value = this.type.createValue( "true", config );

        assertNotNull( value );
        assertSame( ValueTypes.BOOLEAN, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "value" ), config );
    }
}
