package com.enonic.xp.query.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdFilterTest
{

    @Test
    void testBuildler()
    {
        final IdFilter filter = IdFilter.create().
            fieldName( "field1" ).
            value( "node1" ).
            build();

        assertNotNull( filter );
        assertEquals( "field1", filter.getFieldName() );
        assertEquals( 1, filter.getValues().size() );
    }

    @Test
    void testToString()
    {
        final IdFilter filter = IdFilter.create().
            fieldName( "field1" ).
            value( "node1" ).
            value( "node2" ).
            build();

        assertEquals( "IdFilter{fieldName=field1, values=[node1, node2]}", filter.toString() );
    }
}
