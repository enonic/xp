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

class TextLineTypeTest
    extends BaseInputTypeTest
{

    public static final String IP_ADDRESS_REGEXP = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

    public TextLineTypeTest()
    {
        super( TextLineType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "TextLine", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "TextLine", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), GenericValue.newObject().build() );
        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.newObject().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class,
                      () -> this.type.validate( booleanProperty( true ), GenericValue.newObject().build() ) );
    }

    @Test
    void testValidateRegexInvalid()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ) );
    }

    @Test
    void testValidateRegexEmptyValue()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "" ), config ) );
    }

    @Test
    void testValidateRegexValid()
    {
        final GenericValue config = newValidConfig();
        this.type.validate( stringProperty( "10.192.6.144" ), config );
    }

    @Test
    void testValidateMalformedRegex()
    {
        final GenericValue config = newInvalidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ) );
    }

    @Test
    void testValidate_invalidMaxLength()
    {
        final GenericValue config = GenericValue.newObject().put( "maxLength", GenericValue.numberValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ) );
    }

    private GenericValue newValidConfig()
    {
        return GenericValue.newObject().put( "regexp", IP_ADDRESS_REGEXP ).build();
    }

    private GenericValue newInvalidConfig()
    {
        return GenericValue.newObject().put( "regexp", "[" ).build();
    }
}
