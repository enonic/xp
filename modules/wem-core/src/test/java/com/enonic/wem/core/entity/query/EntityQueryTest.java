package com.enonic.wem.core.entity.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.filter.ExistsFilter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.query.parser.QueryParser;

import static org.junit.Assert.*;

public class EntityQueryTest
{
    @Test
    public void build()
        throws Exception
    {
        final EntityQuery entityQuery = EntityQuery.newEntityQuery().
            addPostFilter( ValueFilter.create().
                fieldName( "myField" ).
                addValue( Value.newString( "test1" ) ).
                addValue( Value.newString( "test2" ) ).
                build() ).
            addPostFilter( ExistsFilter.create().
                fieldName( "myPossiblyExistingField" ).
                build() ).
            query( QueryParser.parse( "data/test > 3 ORDER BY test ASC" ) ).
            build();

        assertNotNull( entityQuery.getQuery() );
        assertEquals( 2, entityQuery.getPostFilters().getSize() );

    }
}
