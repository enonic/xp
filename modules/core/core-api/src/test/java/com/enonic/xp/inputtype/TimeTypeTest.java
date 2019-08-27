package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTypeTest
    extends BaseInputTypeTest
{
    public TimeTypeTest()
    {
        super( TimeType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Time", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Time", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "22:11:00" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TIME, "08:08:08" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
        assertEquals( value.toString(), "08:08:08" );

    }

    @Test
    public void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.TIME, "25:08:08" ).build();

        assertThrows(IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( localTimeProperty(), config );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }
}
