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

public class TextLineTypeTest
    extends BaseInputTypeTest
{

    public static final String IP_ADDRESS_REGEXP = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

    public TextLineTypeTest()
    {
        super( TextLineType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "TextLine", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "TextLine", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), config );
        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );

        final Value value2 = this.type.createValue( "test", config );
        assertNotNull( value2 );
        assertSame( ValueTypes.STRING, value2.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TEXT_LINE, "testString" ).build();
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
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    @Test
    public void testValidateRegexInvalid()
    {
        final InputTypeConfig config = newValidConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ));
    }

    @Test
    public void testValidateRegexEmptyValue()
    {
        final InputTypeConfig config = newValidConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "" ), config ));
    }

    @Test
    public void testValidateRegexValid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "10.192.6.144" ), config );
    }

    @Test
    public void testValidateMalformedRegex()
    {
        final InputTypeConfig config = newInvalidConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ));
    }

    @Test
    public void testValidate_invalidMaxLength()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "maxLength", "5" ).build() ).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ));
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().
            property( InputTypeProperty.create( "regexp", IP_ADDRESS_REGEXP ).build() ).
            build();
    }

    private InputTypeConfig newInvalidConfig()
    {
        return InputTypeConfig.create().
            property( InputTypeProperty.create( "regexp", "[" ).build() ).
            build();
    }
}
