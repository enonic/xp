package com.enonic.wem.api.data;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.ValueTypeException;
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
                return Value.newString( "aaa" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{Value.newString( "bbb" ), Value.newHtmlPart( "aaa" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Value.newString( "aaa" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Value.newString( "aaa" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isJavaType()
    {
        assertTrue( Value.newString( "Some text" ).isJavaType( String.class ) );
        assertTrue( Value.newBoolean( false ).isJavaType( Boolean.class ) );
        assertTrue( Value.newLocalDate( LocalDate.now() ).isJavaType( LocalDate.class ) );
    }

    @Test
    public void construct_ContentId()
    {
        ContentId value = ContentId.from( "abc" );

        assertSame( value, Value.newContentId( value ).asContentId() );
        assertEquals( value, Value.newContentId( "abc" ).asContentId() );
    }

    @Test
    public void construct_Date()
    {
        LocalDate value = new LocalDate( 2013, 1, 1 );

        assertSame( value, Value.newLocalDate( value ).asLocalDate() );
        assertEquals( value, Value.newLocalDate( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).asLocalDate() );
        assertEquals( value, Value.newLocalDate( "2013-1-1" ).asLocalDate() );
    }

    @Test
    public void construct_Boolean()
    {
        Boolean value = Value.newBoolean( true ).asBoolean();

        assertEquals( true, value.booleanValue() );
    }

    @Test(expected = ValueTypeException.class)
    public void convert_non_numeric_string_double()
    {
        Value.newString( "test" ).asDouble();
    }

    @Test
    public void convert_numeric_string_double()
    {
        Double doubleValue = Value.newString( "123" ).asDouble();
        assertEquals( 123.0, doubleValue );
    }

    @Test
    public void convert_numeric_string_with_point_double()
    {
        Double doubleValue = Value.newString( "123.5" ).asDouble();
        assertEquals( 123.5, doubleValue );
    }

    @Test
    public void data()
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", Value.newString( "A" ) );

        Value value = Value.newData( data );
        assertTrue( data.valueEquals( value.asData() ) );
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

        Value value = Value.newData( dataAsString );

        RootDataSet expectedData = new RootDataSet();
        expectedData.setProperty( "myProp", Value.newString( "a" ) );
        assertTrue( expectedData.valueEquals( value.asData() ) );
    }

}


