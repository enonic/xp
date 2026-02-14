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

class RadioButtonTypeTest
    extends BaseInputTypeTest
{
    public RadioButtonTypeTest()
    {
        super( RadioButtonType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "RadioButton", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "RadioButton", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "one" ), GenericValue.newObject().build() );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testValidate()
    {
        final GenericValue config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test
    void testValidate_invalid()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "unknown" ), config ) );
    }

    @Test
    void testValidate_invalidType()
    {
        final GenericValue config = GenericValue.newObject().build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    private GenericValue newValidConfig()
    {
        return GenericValue.newObject()
            .put( "options", GenericValue.newList()
                .add( GenericValue.newObject()
                          .put( "value", "one" )
                          .put( "label", GenericValue.newObject().put( "text", "Value One" ).build() )
                          .build() )
                .add( GenericValue.newObject()
                          .put( "value", "two" )
                          .put( "label", GenericValue.newObject().put( "text", "Value Two" ).build() )
                          .build() )
                .build() )
            .build();
    }
}
