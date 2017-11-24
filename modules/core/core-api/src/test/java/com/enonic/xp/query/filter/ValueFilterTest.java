package com.enonic.xp.query.filter;

import org.junit.Test;

import com.enonic.xp.data.ValueFactory;

import static org.junit.Assert.*;

public class ValueFilterTest
{

    @Test
    public void multiple_values()
        throws Exception
    {
        String[] strings = new String[]{"one", "two", "three"};

        final ValueFilter myFilter = ValueFilter.create().
            fieldName( "myfield" ).
            addValues( strings ).
            addValue( ValueFactory.newString( "four" ) ).
            addValues( ValueFactory.newDouble( 2.0 ), ValueFactory.newBoolean( true ) ).
            build();

        assertEquals( 6, myFilter.getValues().size() );
        assertEquals( "myfield", myFilter.getFieldName() );
    }

    @Test
    public void testToString()
    {
        String[] strings = new String[]{"one", "two", "three"};

        final ValueFilter filter = ValueFilter.create().
            fieldName( "myfield" ).
            addValues( strings ).
            addValue( ValueFactory.newString( "four" ) ).
            addValues( ValueFactory.newDouble( 2.0 ), ValueFactory.newBoolean( true ) ).
            build();

        assertEquals( "ValueFilter{fieldName=myfield, values=[2.0, true, one, four, two, three]}", filter.toString() );
    }
}