package com.enonic.xp.query.filter;

import org.junit.Test;

import com.enonic.xp.data.ValueFactory;

import static org.junit.Assert.*;

public class BooleanFilterTest
{

    @Test
    public void testBuildler()
    {
        final IdFilter filter1 = IdFilter.create().
            fieldName( "field1" ).
            value( "node1" ).
            build();
        final IndicesFilter filter2 = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).build() ).
            build();
        final RangeFilter filter3 = RangeFilter.create().
            from( ValueFactory.newDouble( 2.0 ) ).
            to( ValueFactory.newDouble( 50.0 ) ).
            setCache( true ).
            build();

        final BooleanFilter filter = BooleanFilter.create().
            must( filter1 ).
            mustNot( filter2 ).
            should( filter3 ).
            build();

        assertNotNull( filter );
        assertEquals( 1, filter.getMust().size() );
        assertEquals( 1, filter.getMustNot().size() );
        assertEquals( 1, filter.getShould().size() );
    }

    @Test
    public void testToString()
    {
        final IdFilter filter1 = IdFilter.create().
            fieldName( "field1" ).
            value( "node1" ).
            build();
        final IndicesFilter filter2 = IndicesFilter.create().
            addIndices( "index1" ).
            filter( IdFilter.create().value( "value1" ).build() ).
            noMatchFilter( IdFilter.create().value( "value2" ).build() ).
            build();
        final RangeFilter filter3 = RangeFilter.create().
            from( ValueFactory.newDouble( 2.0 ) ).
            to( ValueFactory.newDouble( 50.0 ) ).
            setCache( true ).
            build();

        final BooleanFilter filter = BooleanFilter.create().
            must( filter1 ).
            mustNot( filter2 ).
            should( filter3 ).
            build();

        assertEquals(
            "BooleanFilter{must=[IdFilter{fieldName=field1, values=[node1]}], mustNot=[IndicesFilter{indices=[index1], filter=IdFilter{values=[value1]}, noMatchFilter=IdFilter{values=[value2]}}], should=[RangeFilter{from=2.0, to=50.0}]}",
            filter.toString() );
    }
}
