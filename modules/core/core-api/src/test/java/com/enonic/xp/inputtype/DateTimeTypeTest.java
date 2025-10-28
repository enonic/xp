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

class DateTimeTypeTest
    extends BaseInputTypeTest
{
    public DateTimeTypeTest()
    {
        super( DateTimeType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "DateTime", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "DateTime", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02T22:11:00" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE_TIME, value.getType() );
    }

    @Test
    void testValidate_dateTime()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( dateTimeProperty(), GenericValue.object().build() ) );
    }

    @Test
    void testValidate_localDateTime()
    {
        this.type.validate( localDateTimeProperty(), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class,
                      () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }
}
