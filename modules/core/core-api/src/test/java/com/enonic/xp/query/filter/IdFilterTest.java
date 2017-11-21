package com.enonic.xp.query.filter;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdFilterTest
{

    @Test
    public void testBuildler()
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
    public void testToString()
    {
        final IdFilter filter = IdFilter.create().
            fieldName( "field1" ).
            value( "node1" ).
            value( "node2" ).
            build();

        assertEquals( "IdFilter{fieldName=field1, values=[node1, node2]}", filter.toString() );
    }
}