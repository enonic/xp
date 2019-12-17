package com.enonic.xp.query.filter;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeFilterTest
{
    @Test
    public void testBuilder()
    {
        final RangeFilter rangeFilter = RangeFilter.create().
            from( ValueFactory.newDouble( 2.0 ) ).
            to( ValueFactory.newDouble( 50.0 ) ).
            setCache( true ).
            build();

        assertNotNull( rangeFilter );
        assertEquals( ValueFactory.newDouble( 2.0 ), rangeFilter.getFrom() );
        assertEquals( ValueFactory.newDouble( 50.0 ), rangeFilter.getTo() );
        assertTrue( rangeFilter.isCache() );
    }

    @Test
    public void testToString()
    {
        final RangeFilter filter = RangeFilter.create().
            from( ValueFactory.newDouble( 2.0 ) ).
            to( ValueFactory.newDouble( 50.0 ) ).
            setCache( true ).
            build();

        assertEquals( "RangeFilter{from=2.0, to=50.0}", filter.toString() );
    }
}
