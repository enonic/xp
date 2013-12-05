package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.filter.Filter;
import com.enonic.wem.query.parser.QueryParser;

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
            build();

        assertNotNull( entityQuery.getQuery() );
        assertEquals( 2, entityQuery.getFilters().size() );


    }
}
