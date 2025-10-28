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

class TimeTypeTest
    extends BaseInputTypeTest
{
    public TimeTypeTest()
    {
        super( TimeType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Time", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Time", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "22:11:00" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TIME, "08:08:08" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
        assertEquals( value.toString(), "08:08:08" );

    }

    @Test
    void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TIME, "25:08:08" ).build();

        assertThrows(IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( localTimeProperty(), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }
}
