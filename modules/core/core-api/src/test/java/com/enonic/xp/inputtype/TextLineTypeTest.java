package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

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
        final Value value = this.type.createValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final InputTypeDefault config =
            InputTypeDefault.create().
                property( InputTypeProperty.create( "default", "testString" ).
                    build() ).
                build();

        final Value value = this.type.createDefaultValue( config );

        assertNotNull( value );
        assertEquals( "testString", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidateRegexInvalid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "abc" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidateRegexEmptyValue()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "" ), config );
    }

    @Test
    public void testValidateRegexValid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "10.192.6.144" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidateMalformedRegex()
    {
        final InputTypeConfig config = newInvalidConfig();
        this.type.validate( stringProperty( "abc" ), config );
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
