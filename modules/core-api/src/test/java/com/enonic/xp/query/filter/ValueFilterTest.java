package com.enonic.xp.query.filter;

import org.junit.Test;

import com.enonic.xp.query.filter.ValueFilter;

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
            build();

        assertEquals( 3, myFilter.getValues().size() );
    }
}