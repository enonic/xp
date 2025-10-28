package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagTypeTest
    extends BaseInputTypeTest
{
    public TagTypeTest()
    {
        super( TagType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Tag", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Tag", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }
}

