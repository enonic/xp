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

public class TextAreaTypeTest
    extends BaseInputTypeTest
{
    public TextAreaTypeTest()
    {
        super( TextAreaType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "TextArea", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "TextArea", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TEXT_AREA, "testString" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "testString", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    @Test
    public void testValidate_invalidMaxLength()
    {
        final InputTypeConfig config =
            InputTypeConfig.create().property( InputTypeProperty.create( "maxLength", PropertyValue.longValue( 5 ) ).build() ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ) );
    }
}
