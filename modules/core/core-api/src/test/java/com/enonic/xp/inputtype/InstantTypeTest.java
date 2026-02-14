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

public class InstantTypeTest
    extends BaseInputTypeTest
{
    public InstantTypeTest()
    {
        super( InstantType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Instant", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Instant", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02T22:11:00Z" ), GenericValue.newObject().build() );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
    }

    @Test
    public void testValidate_dateTime()
    {
        this.type.validate( instantProperty(), GenericValue.newObject().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.newObject().build() ) );
    }
}
