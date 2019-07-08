package com.enonic.xp.query.suggestion;

import org.junit.Test;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

import static org.junit.Assert.*;

public class SuggestionQueriesTest
{
    @Test
    public void testBuilder()
    {
        final TermSuggestionQuery query = TermSuggestionQuery.create( "query" ).
            field( "fieldName" ).
            build();

        final SuggestionQueries queries = SuggestionQueries.create().
            add( query ).
            build();

        assertNotNull( queries );
        assertTrue( queries.isNotEmpty() );
        assertTrue( queries.contains( query ) );
    }

    @Test
    public void testEmpty()
    {
        final SuggestionQueries queries = SuggestionQueries.empty();

        assertTrue( queries.isEmpty() );
    }
}
