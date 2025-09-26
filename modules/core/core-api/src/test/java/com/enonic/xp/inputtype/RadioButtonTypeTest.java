package com.enonic.xp.inputtype;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final Value value = this.type.createValue( ValueFactory.newString( "one" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.RADIO_BUTTON, "testOption" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "testOption", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = newValidConfig();
        this.type.validate( stringProperty( "one" ), config );
    }

    @Test
    public void testValidate_invalid()
    {
        final InputTypeConfig config = newValidConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( stringProperty( "unknown" ), config ) );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    private InputTypeConfig newValidConfig()
    {
        return InputTypeConfig.create().property( InputTypeProperty.create( "option", new ObjectPropertyValue( new LinkedHashMap<>()
        {{
            put( "value", new StringPropertyValue( "one" ) );
            put( "label", new ObjectPropertyValue( new LinkedHashMap<>()
            {{
                put( "text", new StringPropertyValue( "Value One" ) );
            }} ) );
        }} ) ).build() ).property( InputTypeProperty.create( "option", new ObjectPropertyValue( new LinkedHashMap<>()
        {{
            put( "value", new StringPropertyValue( "two" ) );
            put( "label", new ObjectPropertyValue( new LinkedHashMap<>()
            {{
                put( "text", new StringPropertyValue( "Value Two" ) );
            }} ) );
        }} ) ).build() ).build();
    }
}
