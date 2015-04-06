package com.enonic.xp.content.query;

import org.junit.Test;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.ContentId;

import static org.junit.Assert.*;

public class ContentQueryResultTest
{
    @Test
    public void testBuilder()
    {
        final ContentQueryResult queryResult = ContentQueryResult.newResult( 5 ).
            addContentHit( ContentId.from( "testId" ), 3 ).
            setAggregations( Aggregations.empty() ).
            build();

        assertNotNull( queryResult );
        assertEquals( 5, queryResult.getTotalSize() );
        assertEquals( 1, queryResult.getContentIds().size() );
        assertTrue( queryResult.getAggregations().isEmpty() );
        assertEquals( 1, queryResult.getContentQueryHits().size() );
    }
}
