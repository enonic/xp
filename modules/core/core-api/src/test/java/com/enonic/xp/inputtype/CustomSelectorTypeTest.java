package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomSelectorTypeTest
    extends BaseInputTypeTest
{
    public CustomSelectorTypeTest()
    {
        super( CustomSelectorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "CustomSelector", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "CustomSelector", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "name" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( stringProperty( "name" ), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ));
    }
}
