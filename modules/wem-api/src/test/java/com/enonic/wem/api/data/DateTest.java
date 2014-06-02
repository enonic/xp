package com.enonic.wem.api.data;

import java.time.LocalDate;

import org.junit.Test;

import com.enonic.wem.api.data.type.ValueTypeException;

import static junit.framework.Assert.assertEquals;

public class DateTest
{
    @Test
    public void given_builder_with_value_of_type_DateMidnight_then_getDate_returns_equal()
    {
        Property date = Property.newLocalDate( "myDate", LocalDate.of( 2013, 1, 1 ) );
        assertEquals( LocalDate.of( 2013, 1, 1 ), date.getLocalDate() );
    }

    @Test
    public void given_builder_with_a_legal_date_String_as_value_then_getDate_returns_DateMidnight_of_same_date()
    {
        Property date = Property.newLocalDate( "myDate", "2013-01-01" );
        assertEquals( LocalDate.of( 2013, 1, 1 ), date.getLocalDate() );
    }

    @Test(expected = ValueTypeException.class)
    public void given_builder_with_a_illegal_date_String_as_value_then_IllegalArgumentException_is_thrown()
    {
        Property date = Property.newLocalDate( "myDate", "2013-34-43" );
        assertEquals( LocalDate.of( 2013, 1, 1 ), date.getLocalDate() );
    }
}
