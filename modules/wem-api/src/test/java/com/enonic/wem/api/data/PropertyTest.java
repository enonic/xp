package com.enonic.wem.api.data;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.data.type.ValueTypes;

import static com.enonic.wem.api.data.Property.GeographicCoordinate.newGeographicCoordinate;
import static com.enonic.wem.api.data.Property.newProperty;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
                return newProperty().name( "myData" ).type( ValueTypes.STRING ).value( "aaa" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{newProperty().name( "myData" ).type( ValueTypes.STRING ).value( "bbb" ).build(),
                    newProperty().name( "myOtherData" ).type( ValueTypes.STRING ).value( "aaa" ).build(),
                    newProperty().name( "myData" ).type( ValueTypes.HTML_PART ).value( "aaa" ).build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return newProperty().name( "myData" ).type( ValueTypes.STRING ).value( "aaa" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return newProperty().name( "myData" ).type( ValueTypes.STRING ).value( "aaa" ).build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void valueEquals()
    {
        assertTrue( new Property.String( "myProperty", "MyValue" ).equals( new Property.String( "myProperty", "MyValue" ) ) );
        assertFalse( new Property.String( "myProperty", "MyValue" ).valueEquals( new Property.Xml( "myProperty", "MyValue" ) ) );
        assertFalse( new Property.String( "myProperty", "MyValue" ).valueEquals( new Property.String( "myProperty", "AnotherValue" ) ) );
    }

    @Test
    public void getDouble_given_value_as_long()
    {
        Property property = newProperty().name( "myNumber" ).type( ValueTypes.LONG ).value( (long) 2 ).build();
        assertEquals( Double.class, property.getDouble().getClass() );
        assertEquals( 2.0, property.getDouble() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        newProperty().name( "myDate" ).type( ValueTypes.DATE_MIDNIGHT ).value( "2012.31.08" ).build();
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Property property = newProperty().name( "myNumber" ).type( ValueTypes.LONG ).value( 1l ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Property property = newProperty().name( "myNumber" ).type( ValueTypes.DOUBLE ).value( 1.1 ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Property property = newProperty().name( "myText" ).type( ValueTypes.STRING ).value( "1" ).build();
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getBinaryId_given_value_as_string()
    {
        Property property =
            newProperty().name( "myBinary" ).type( ValueTypes.BINARY_ID ).value( "217482f4-b89a-4286-9111-5120d11da6c2" ).build();
        assertEquals( BinaryId.from( "217482f4-b89a-4286-9111-5120d11da6c2" ), property.getBinaryId() );
    }

    @Test
    public void geographicalCoordinate()
    {
        assertEquals( "90,123", newProperty( "myGeo" ).type( ValueTypes.GEO_POINT ).value( "90,123" ).build().getString() );
        assertEquals( "90,123", newGeographicCoordinate( "myGeo" ).value( "90,123" ).getString() );
        assertEquals( "90,123", newGeographicCoordinate().name( "myGeo" ).value( "90,123" ).build().getString() );
        assertEquals( "90,123", new Property.GeographicCoordinate( "myGeo", "90,123" ).getString() );
    }
}
