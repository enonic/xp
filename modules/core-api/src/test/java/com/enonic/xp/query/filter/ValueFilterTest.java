package com.enonic.xp.query.filter;

import org.junit.Test;

import com.enonic.xp.data.Value;

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
            addValue( Value.newString( "four" ) ).
            addValues( Value.newDouble( 2.0 ), Value.newBoolean( true ) ).
            build();

        assertEquals( 6, myFilter.getValues().size() );
        assertEquals( "myfield", myFilter.getFieldName() );
    }
}