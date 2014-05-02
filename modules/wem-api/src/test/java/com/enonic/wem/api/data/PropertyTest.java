package com.enonic.wem.api.data;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

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
                return Property.newString( "myData", "aaa" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{Property.newString( "myData", "bbb" ), Property.newString( "myOtherData", "aaa" ),
                    Property.newHtmlPart( "myData", "aaa" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Property.newString( "myData", "aaa" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Property.newString( "myData", "aaa" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void valueEquals()
    {
        assertTrue( Property.newString( "myProperty", "MyValue" ).equals( Property.newString( "myProperty", "MyValue" ) ) );
        assertFalse( Property.newString( "myProperty", "MyValue" ).valueEquals( Property.newXml( "myProperty", "MyValue" ) ) );
        assertFalse( Property.newString( "myProperty", "MyValue" ).valueEquals( Property.newString( "myProperty", "AnotherValue" ) ) );
    }

    @Test
    public void getBoolean_given_value_as_string()
    {
        Property property = Property.newBoolean( "mySwitch", true );
        assertEquals( Boolean.class, property.getBoolean().getClass() );
        assertEquals( true, property.getBoolean().booleanValue() );
    }

    @Test
    public void getDouble_given_value_as_long()
    {
        Property property = Property.newLong( "myNumber", 2 );
        assertEquals( Double.class, property.getDouble().getClass() );
        assertEquals( 2.0, property.getDouble() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        Property.newDateMidnight( "myDate", "2012.31.08" );
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Property property = Property.newLong( "myNumber", 1l );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Property property = Property.newDouble( "myNumber", 1.1 );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Property property = Property.newString( "myText", "1" );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void geographicalCoordinate()
    {
        assertEquals( "90,123", Property.newGeoPoint( "myGeo", "90,123" ).getString() );
    }

    @Test
    public void data()
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", Value.newString( "A" ) );

        Property property = Property.newData( "myData", data );
        assertTrue( data.valueEquals( property.getData() ) );
    }
}
