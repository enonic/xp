package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.data.type.DataTypes;

import static com.enonic.wem.api.content.data.Data.newData;
import static junit.framework.Assert.assertEquals;

public class DataTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "bbb" ).build(),
                    Data.newData().name( "myOtherData" ).type( DataTypes.TEXT ).value( "aaa" ).build(),
                    Data.newData().name( "myData" ).type( DataTypes.HTML_PART ).value( "aaa" ).build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "aaa" ).build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void getDate_given_value_as_string()
    {
        Data data = newData().name( "myDate" ).type( DataTypes.TEXT ).value( "2012-08-31" ).build();
        assertEquals( DateMidnight.class, data.getDate().getClass() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), data.getDate() );
    }

    @Test
    public void getDouble_given_value_as_long()
    {
        Data data = newData().name( "myNumber" ).type( DataTypes.WHOLE_NUMBER ).value( (long) 2 ).build();
        assertEquals( Double.class, data.getDouble().getClass() );
        assertEquals( 2.0, data.getDouble() );
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
        assertEquals( new Long( 1 ), data.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Data data = newData().name( "myNumber" ).type( DataTypes.DECIMAL_NUMBER ).value( 1.1 ).build();
        assertEquals( new Long( 1 ), data.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Data data = newData().name( "myText" ).type( DataTypes.TEXT ).value( "1" ).build();
        assertEquals( new Long( 1 ), data.getLong() );
    }
}
