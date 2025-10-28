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
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TEXT_AREA, "testString" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "testString", value.toString() );

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
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    @Test
    void testValidate_invalidMaxLength()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "maxLength", "5" ).build() ).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ));
    }
}
