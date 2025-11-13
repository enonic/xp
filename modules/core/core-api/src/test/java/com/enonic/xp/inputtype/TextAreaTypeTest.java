package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextAreaTypeTest
    extends BaseInputTypeTest
{
    public TextAreaTypeTest()
    {
        super( TextAreaType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "TextArea", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "TextArea", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    void testValidate_invalidMaxLength()
    {
        final GenericValue config =
            GenericValue.object().put( "maxLength", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ) );
    }
}
