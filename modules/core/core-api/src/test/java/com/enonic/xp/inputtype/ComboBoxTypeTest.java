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

class ComboBoxTypeTest
    extends BaseInputTypeTest
{
    public ComboBoxTypeTest()
    {
        super( ComboBoxType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "ComboBox", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "ComboBox", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "one" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.COMBO_BOX, "testOption" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "testOption", value.toString() );

    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test
    void testValidate_invalidValue()
    {
        final InputTypeConfig config = newValidConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "unknown" ), config ));
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = newValidConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().
            property( InputTypeProperty.create( "option", "Value One" ).attribute( "value", "one" ).build() ).
            property( InputTypeProperty.create( "option", "Value Two" ).attribute( "value", "two" ).build() ).
            build();
    }
}
