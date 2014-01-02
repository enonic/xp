package com.enonic.wem.api.query;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.EntityQuery;
import com.enonic.wem.api.query.facet.FacetQuery;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.parser.QueryParser;

import static org.junit.Assert.*;

public class EntityQueryTest
{
    @Test
    public void build()
        throws Exception
    {
        final EntityQuery entityQuery = EntityQuery.newQuery().
            addFilter( Filter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "test1" ), new Value.String( "test2" ) ).
                build() ).
            addFilter( Filter.newContentTypeFilter().
                add( "myContentTypeId", "myOtherContentTypeId" ).
                build() ).
            query( QueryParser.parse( "data/test > 3 ORDER BY test ASC" ) ).
            addFacet( FacetQuery.newTermsFacetQuery( "myFacetQuery" ).
                fields( Lists.newArrayList( "myField" ) ).
                build() ).
            build();

        assertNotNull( entityQuery.getQuery() );
        assertEquals( 2, entityQuery.getFilters().size() );
        assertEquals( 1, entityQuery.getFacetQueries().size() );


    }
}
