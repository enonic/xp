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

public class CheckBoxTypeTest
    extends BaseInputTypeTest
{
    public CheckBoxTypeTest()
    {
        super( CheckBoxType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "CheckBox", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "CheckBox", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "true" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.BOOLEAN, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.CHECK_BOX, "checked" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "true", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( stringProperty( "value" ), config ));
    }
}
