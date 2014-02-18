package com.enonic.wem.api.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.query.filter.FieldFilter;
import com.enonic.wem.api.query.parser.QueryParser;

import static org.junit.Assert.*;

public class EntityQueryTest
{
    @Test
    public void build()
        throws Exception
    {
        final EntityQuery entityQuery = EntityQuery.newQuery().
            addFilter( FieldFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "test1" ), new Value.String( "test2" ) ).
                build() ).
            addFilter( FieldFilter.newContentTypeFilter().
                add( "myContentTypeId", "myOtherContentTypeId" ).
                build() ).
            query( QueryParser.parse( "data/test > 3 ORDER BY test ASC" ) ).
            build();

        assertNotNull( entityQuery.getQuery() );
        assertEquals( 2, entityQuery.getFilters().size() );

    }
}
