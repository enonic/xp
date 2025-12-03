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

class DateTypeTest
    extends BaseInputTypeTest
{
    public DateTypeTest()
    {
        super( DateType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Date", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Date", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( localDateProperty(), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }
}
