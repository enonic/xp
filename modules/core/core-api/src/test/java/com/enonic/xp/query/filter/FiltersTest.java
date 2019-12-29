package com.enonic.xp.query.filter;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FiltersTest
{
    @Test
    public void testBuilder()
    {
        final Filters filters = Filters.create().
            add( ExistsFilter.create().build() ).
            addAll( List.of( ExistsFilter.create().build(), ExistsFilter.create().build() ) ).
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
