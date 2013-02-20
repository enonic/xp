package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.datatype.DataTypes;

import static com.enonic.wem.api.content.data.Data.newData;
import static junit.framework.Assert.assertEquals;

public class DataTest
{
    @Test
    public void getDate_given_value_as_string()
    {
        Data data = newData().name( "myDate" ).type( DataTypes.TEXT ).value( "2012-08-31" ).build();
        assertEquals( DateMidnight.class, data.asDate().getClass() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), data.asDate() );
    }

    @Test
    public void getDouble_given_value_as_long()
    {
        Data data = newData().name( "myNumber" ).type( DataTypes.WHOLE_NUMBER ).value( (long) 2 ).build();
        assertEquals( Double.class, data.asDouble().getClass() );
        assertEquals( 2.0, data.asDouble() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        newData().name( "myDate" ).type( DataTypes.DATE ).value( "2012.31.08" ).build();
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Data data = newData().name( "myNumber" ).type( DataTypes.WHOLE_NUMBER ).value( 1l ).build();
        assertEquals( new Long( 1 ), data.asLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Data data = newData().name( "myNumber" ).type( DataTypes.DECIMAL_NUMBER ).value( 1.1 ).build();
        assertEquals( new Long( 1 ), data.asLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Data data = newData().name( "myText" ).type( DataTypes.TEXT ).value( "1" ).build();
        assertEquals( new Long( 1 ), data.asLong() );
    }
}
