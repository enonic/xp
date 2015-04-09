package com.enonic.xp.query.filter;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanFilterTest
{

    @Test
    public void testBuildler()
    {
        final BooleanFilter filter = BooleanFilter.create().
            must( RangeFilter.create().build() ).
            mustNot( ExistsFilter.create().build() ).
            should( ValueFilter.create().build() ).
            build();

        assertNotNull( filter );
        assertEquals( 1, filter.getMust().size() );
        assertEquals( 1, filter.getMustNot().size() );
        assertEquals( 1, filter.getShould().size() );
    }
}
