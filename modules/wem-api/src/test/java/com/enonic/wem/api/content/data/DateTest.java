package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DateTest
{

    @Test
    public void given_builder_with_value_of_type_DateMidnight_then_getDate_returns_equal()
    {
        Data.Date date = Data.newDate().name( "myDate" ).value( new DateMidnight( 2013, 1, 1 ) ).build();
        assertEquals( new DateMidnight( 2013, 1, 1 ), date.getDate() );
    }

    @Test
    public void given_builder_with_a_legal_date_String_as_value_then_getDate_returns_DateMidnight_of_same_date()
    {
        Data.Date date = Data.newDate().name( "myDate" ).value( "2013-01-01" ).build();
        assertEquals( new DateMidnight( 2013, 1, 1 ), date.getDate() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_builder_with_a_illegal_date_String_as_value_then_IllegalArgumentException_is_thrown()
    {
        Data.Date date = Data.newDate().name( "myDate" ).value( "2013-34-43" ).build();
        assertEquals( new DateMidnight( 2013, 1, 1 ), date.getDate() );
    }
}
