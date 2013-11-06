package com.enonic.wem.api.data;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.support.AbstractEqualsTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class ValueTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new Value.String( "aaa" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new Value.String( "bbb" ), new Value.HtmlPart( "aaa" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new Value.String( "aaa" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new Value.String( "aaa" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isJavaType()
    {
        assertTrue( new Value.String( "Some text" ).isJavaType( String.class ) );
        assertTrue( new Value.DateMidnight( org.joda.time.DateMidnight.now() ).isJavaType( org.joda.time.DateMidnight.class ) );
    }

    @Test
    public void construct_ContentId()
    {
        ContentId value = ContentId.from( "abc" );

        assertSame( value, new Value.ContentId( value ).getContentId() );
        assertEquals( value, new Value.ContentId( "abc" ).getContentId() );
    }

    @Test
    public void construct_BinaryId()
    {
        BinaryId value = BinaryId.from( "abc" );

        assertSame( value, new Value.BinaryId( value ).getBinaryId() );
        assertEquals( value, new Value.BinaryId( "abc" ).getBinaryId() );
    }

    @Test
    public void construct_Date()
    {
        org.joda.time.DateMidnight value = new org.joda.time.DateMidnight( 2013, 1, 1 );

        assertSame( value, new Value.DateMidnight( value ).getDate() );
        assertEquals( value, new Value.DateMidnight( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).getDate() );
        assertEquals( value, new Value.DateMidnight( "2013-1-1" ).getDate() );
    }

    @Test(expected = InconvertibleValueException.class)
    public void convert_non_numeric_string_double()
    {
        new Value.String( "test" ).asDouble();
    }

    @Test
    public void convert_numeric_string_double()
    {
        Double doubleValue = new Value.String( "123" ).asDouble();
        assertEquals( 123.0, doubleValue );
    }

    @Test
    public void convert_numeric_string_with_point_double()
    {
        Double doubleValue = new Value.String( "123.5" ).asDouble();
        assertEquals( 123.5, doubleValue );
    }

    @Test
    public void data()
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", new Value.String( "A" ) );

        Value value = new Value.Data( data );
        assertTrue( data.valueEquals( value.getData() ) );
    }

    @Test
    public void data_given_value_as_string()
    {
        String dataAsString = "[\n" +
            "    {\n" +
            "        \"name\": \"myProp\",\n" +
            "        \"type\": \"String\",\n" +
            "        \"value\": \"a\"\n" +
            "    }\n" +
            "]";

        Value value = new Value.Data( dataAsString );

        RootDataSet expectedData = new RootDataSet();
        expectedData.setProperty( "myProp", new Value.String( "a" ) );
        assertTrue( expectedData.valueEquals( value.getData() ) );
    }

}


