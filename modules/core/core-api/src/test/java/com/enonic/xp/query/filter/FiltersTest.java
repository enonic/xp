package com.enonic.xp.query.filter;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FiltersTest
{
    @Test
    public void testBuilder()
    {
        final Filters filters = Filters.create().
            add( ExistsFilter.create().build() ).
            addAll( Lists.newArrayList( ExistsFilter.create().build(), ExistsFilter.create().build() ) ).
            build();

        assertNotNull( filters );
        assertEquals( 3, filters.getSize() );
    }

    @Test
    public void from()
    {
        final Filters filters = Filters.from( ExistsFilter.create().build(), ExistsFilter.create().build() );

        assertNotNull( filters );
        assertEquals( 2, filters.getSize() );
    }
}
