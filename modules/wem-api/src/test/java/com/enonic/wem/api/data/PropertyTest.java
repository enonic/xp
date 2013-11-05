package com.enonic.wem.api.data;


import org.junit.Test;

import com.enonic.wem.api.content.binary.BinaryId;
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
                return new Property.String( "myData", "aaa" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new Property.String( "myData", "bbb" ), new Property.String( "myOtherData", "aaa" ),
                    new Property.HtmlPart( "myData", "aaa" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new Property.String( "myData", "aaa" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new Property.String( "myData", "aaa" );
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
        Property property = new Property.Long( "myNumber", 2 );
        assertEquals( Double.class, property.getDouble().getClass() );
        assertEquals( 2.0, property.getDouble() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        new Property.Date( "myDate", "2012.31.08" );
    }

    @Test
    public void getLong_given_value_as_long()
    {
        Property property = new Property.Long( "myNumber", 1l );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_decimal_number()
    {
        Property property = new Property.Double( "myNumber", 1.1 );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getLong_given_value_as_string()
    {
        Property property = new Property.String( "myText", "1" );
        assertEquals( new Long( 1 ), property.getLong() );
    }

    @Test
    public void getBinaryId_given_value_as_string()
    {
        Property property = new Property.BinaryId( "myBinary", new BinaryId( "217482f4-b89a-4286-9111-5120d11da6c2" ) );
        assertEquals( BinaryId.from( "217482f4-b89a-4286-9111-5120d11da6c2" ), property.getBinaryId() );
    }

    @Test
    public void geographicalCoordinate()
    {
        assertEquals( "90,123", new Property.GeographicCoordinate( "myGeo", "90,123" ).getString() );
    }
}
