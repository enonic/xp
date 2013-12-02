package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.parser.QueryParser;
import com.enonic.wem.query.queryfilter.QueryFilter;

import static org.junit.Assert.*;

public class EntityQueryTest
{
    @Test
    public void build()
        throws Exception
    {
        final EntityQuery entityQuery = EntityQuery.newQuery().
            addFilter( QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "test1" ), new Value.String( "test2" ) ).
                build() ).
            addFilter( QueryFilter.newContentTypeFilter().
                add( "myContentTypeId", "myOtherContentTypeId" ).
                build() ).
            query( QueryParser.parse( "data/test > 3" ) ).
            build();

        assertNotNull( entityQuery.getQuery() );
        assertEquals( 2, entityQuery.getFilters().size() );

    }
}
