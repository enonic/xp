package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.type.PropertyTypes;

import static junit.framework.Assert.assertEquals;

public class PropertyTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "bbb" ).build(),
                    Property.newProperty().name( "myOtherData" ).type( PropertyTypes.TEXT ).value( "aaa" ).build(),
                    Property.newProperty().name( "myData" ).type( PropertyTypes.HTML_PART ).value( "aaa" ).build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Property.newProperty().name( "myData" ).type( PropertyTypes.TEXT ).value( "aaa" ).build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void getDate_given_value_as_string()
    {
        Property property = Property.newProperty().name( "myDate" ).type( PropertyTypes.TEXT ).value( "2012-08-31" ).build();
        assertEquals( DateMidnight.class, property.getDate().getClass() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), property.getDate() );
    }

    @Test
    public void getDouble_given_value_as_long()
    {
        Property property = Property.newProperty().name( "myNumber" ).type( PropertyTypes.WHOLE_NUMBER ).value( (long) 2 ).build();
        assertEquals( Double.class, property.getDouble().getClass() );
        assertEquals( 2.0, property.getDouble() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        Property.newProperty().name( "myDate" ).type( PropertyTypes.DATE_MIDNIGHT ).value( "2012.31.08" ).build();
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Property property = Property.newProperty().name( "myNumber" ).type( PropertyTypes.WHOLE_NUMBER ).value( 1l ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Property property = Property.newProperty().name( "myNumber" ).type( PropertyTypes.DECIMAL_NUMBER ).value( 1.1 ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Property property = Property.newProperty().name( "myText" ).type( PropertyTypes.TEXT ).value( "1" ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void xxx()
    {
        Property property = Property.newProperty().name( "myBinary" ).type( PropertyTypes.BINARY_ID ).value(
            "217482f4-b89a-4286-9111-5120d11da6c2" ).build();
        assertEquals( BinaryId.from( "217482f4-b89a-4286-9111-5120d11da6c2" ), property.getBinaryId() );
    }
}
