package com.enonic.xp.query.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExistsFilterTest
{

    @Test
    void testBuildler()
    {
        final ExistsFilter filter = ExistsFilter.create().
            fieldName( "field1" ).
            build();

        assertNotNull( filter );
        assertEquals( "field1", filter.getFieldName() );
    }

    @Test
    void testToString()
    {
        final ExistsFilter filter = ExistsFilter.create().
            fieldName( "field1" ).
            build();

        assertEquals( "ExistsFilter{fieldName=field1}", filter.toString() );
    }
}
