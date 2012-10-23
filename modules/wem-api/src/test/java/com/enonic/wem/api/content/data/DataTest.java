package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InconvertibleValueException;

import static com.enonic.wem.api.content.data.Data.newData;
import static junit.framework.Assert.assertEquals;

public class DataTest
{
    @Test
    public void given_data_of_type_date_and_value_as_string_when_build_then_value_is_converted_to_dateMidnight()
    {
        Data data = newData().type( DataTypes.DATE ).value( "2012-08-31" ).build();
        assertEquals( DateMidnight.class, data.getValue().getClass() );
    }

    @Test
    public void given_data_of_type_decimalNumber_and_value_as_long_when_build_then_value_is_converted_to_double()
    {
        Data data = newData().type( DataTypes.DECIMAL_NUMBER ).value( (long) 2 ).build();
        assertEquals( Double.class, data.getValue().getClass() );
    }

    @Test(expected = InconvertibleValueException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        newData().type( DataTypes.DATE ).value( "2012.31.08" ).build();
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Data data = newData().type( DataTypes.WHOLE_NUMBER ).value( 1 ).build();
        assertEquals( new Long( 1 ), data.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Data data = newData().type( DataTypes.DECIMAL_NUMBER ).value( 1.1 ).build();
        assertEquals( new Long( 1 ), data.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Data data = newData().type( DataTypes.TEXT ).value( "1" ).build();
        assertEquals( new Long( 1 ), data.getLong() );
    }
}
