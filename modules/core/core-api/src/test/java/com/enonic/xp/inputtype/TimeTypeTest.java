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
        final Value value = this.type.createValue( ValueFactory.newString( "22:11:00" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( localTimeProperty(), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class,
                      () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }
}
