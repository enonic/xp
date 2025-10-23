package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComboBoxTypeTest
    extends BaseInputTypeTest
{
    public ComboBoxTypeTest()
    {
        super( ComboBoxType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ComboBox", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ComboBox", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "one" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final GenericValue config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test
    public void testValidate_invalidValue()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "unknown" ), config ) );
    }

    @Test
    public void testValidate_invalidType()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    private GenericValue newValidConfig()
    {
        return GenericValue.object()
            .put( "option", GenericValue.list()
                .add( GenericValue.object()
                          .put( "value", "one" )
                          .put( "label", GenericValue.object().put( "text", "Value One" ).build() )
                          .build() )
                .add( GenericValue.object()
                          .put( "value", "two" )
                          .put( "label", GenericValue.object().put( "text", "Value Two" ).build() )
                          .build() )
                .build() )
            .build();
    }
}
