package com.enonic.xp.query.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IndicesFilterTest
{

    @Test
    void testBuildler()
    {
        final IndicesFilter filter = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).fieldName( "fieldName1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).fieldName( "fieldName2" ).build() ).
            build();

        assertNotNull( filter );
    }

    @Test
    void testToString()
    {
        final IndicesFilter filter = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).fieldName( "fieldName1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).fieldName( "fieldName2" ).build() ).
        build();

        assertEquals( "IndicesFilter{indices=[index1], filter=IdFilter{fieldName=fieldName1, values=[value1]}, noMatchFilter=IdFilter{fieldName=fieldName2, values=[value2]}}",
                      filter.toString() );
    }
}
