package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class RadioButtonTypeTest
    extends BaseInputTypeTest
{
    public RadioButtonTypeTest()
    {
        super( RadioButtonType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "RadioButton", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "RadioButton", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "one", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final InputTypeDefault config =
            InputTypeDefault.create().
                property( InputTypeProperty.create( "default", "testOption" ).
                    build() ).
                build();

        final Value value = this.type.createDefaultValue( config );

        assertNotNull( value );
        assertEquals( "testOption", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalid()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "unknown" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().
            property( InputTypeProperty.create( "option", "Value One" ).attribute( "value", "one" ).build() ).
            property( InputTypeProperty.create( "option", "Value Two" ).attribute( "value", "two" ).build() ).
            build();
    }
}
