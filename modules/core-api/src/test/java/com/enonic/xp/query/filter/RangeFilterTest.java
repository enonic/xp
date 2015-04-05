package com.enonic.xp.query.filter;

import org.junit.Test;

import com.enonic.xp.data.Value;

import static org.junit.Assert.*;

public class RangeFilterTest
{
    @Test
    public void testBuilder()
    {
        final RangeFilter rangeFilter = RangeFilter.create().
            from( Value.newDouble( 2.0 ) ).
            to( Value.newDouble( 50.0 ) ).
            setCache( true ).
            build();

        assertNotNull( rangeFilter );
        assertEquals( Value.newDouble( 2.0 ), rangeFilter.getFrom() );
        assertEquals( Value.newDouble( 50.0 ), rangeFilter.getTo() );
        assertTrue( rangeFilter.isCache() );
    }

}
