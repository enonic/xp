package com.enonic.xp.query.filter;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FiltersTest
{
    @Test
    void testBuilder()
    {
        final Filters filters = Filters.create().
            add( ExistsFilter.create().fieldName( "fieldName" ).build() ).
            addAll( List.of( ExistsFilter.create().fieldName( "fieldName" ).build(), ExistsFilter.create().fieldName( "fieldName" ).build() ) ).
            build();

        assertNotNull( filters );
        assertEquals( 3, filters.getSize() );
    }

    @Test
    void from()
    {
        final Filters filters = Filters.from( ExistsFilter.create().fieldName( "fieldName" ).build(), ExistsFilter.create().fieldName( "fieldName" ).build() );

        assertNotNull( filters );
        assertEquals( 2, filters.getSize() );
    }
}
