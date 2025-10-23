package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

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
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), GenericValue.object().build() );
        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class,
                      () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    public void testValidateRegexInvalid()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ) );
    }

    @Test
    public void testValidateRegexEmptyValue()
    {
        final GenericValue config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "" ), config ) );
    }

    @Test
    public void testValidateRegexValid()
    {
        final GenericValue config = newValidConfig();
        this.type.validate( stringProperty( "10.192.6.144" ), config );
    }

    @Test
    public void testValidateMalformedRegex()
    {
        final GenericValue config = newInvalidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "abc" ), config ) );
    }

    @Test
    public void testValidate_invalidMaxLength()
    {
        final GenericValue config = GenericValue.object().put( "maxLength", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "max-length" ), config ) );
    }

    private GenericValue newValidConfig()
    {
        return GenericValue.object().put( "regexp", IP_ADDRESS_REGEXP ).build();
    }

    private GenericValue newInvalidConfig()
    {
        return GenericValue.object().put( "regexp", "[" ).build();
    }
}
