package com.enonic.xp.query.filter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExistsFilterTest
{

    @Test
    public void testBuildler()
    {
        final ExistsFilter filter = ExistsFilter.create().
            fieldName( "field1" ).
            build();

        assertNotNull( filter );
        assertEquals( "field1", filter.getFieldName() );
    }

    @Test
    public void testToString()
    {
        final ExistsFilter filter = ExistsFilter.create().
            fieldName( "field1" ).
            build();

        assertEquals( "ExistsFilter{fieldName=field1}", filter.toString() );
    }
}