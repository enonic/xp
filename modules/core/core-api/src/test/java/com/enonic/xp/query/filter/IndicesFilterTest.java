package com.enonic.xp.query.filter;

import org.junit.Test;

import static org.junit.Assert.*;

public class IndicesFilterTest
{

    @Test
    public void testBuildler()
    {
        final IndicesFilter filter = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).build() ).
            build();

        assertNotNull( filter );
    }

    @Test
    public void testToString()
    {
        final IndicesFilter filter = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).build() ).
            build();

        assertEquals( "IndicesFilter{indices=[index1], filter=IdFilter{values=[value1]}, noMatchFilter=IdFilter{values=[value2]}}",
                      filter.toString() );
    }
}