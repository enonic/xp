package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagTypeTest
    extends BaseInputTypeTest
{
    public TagTypeTest()
    {
        super( TagType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Tag", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Tag", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }
}

