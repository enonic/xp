package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomSelectorTypeTest
    extends BaseInputTypeTest
{
    public CustomSelectorTypeTest()
    {
        super( CustomSelectorType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "CustomSelector", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "CustomSelector", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "name" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.CUSTOM_SELECTOR, "testValue" ).build();
        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "testValue", value.toString() );

    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( stringProperty( "name" ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
