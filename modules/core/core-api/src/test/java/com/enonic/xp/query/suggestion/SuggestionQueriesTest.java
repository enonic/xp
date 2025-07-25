package com.enonic.xp.query.suggestion;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuggestionQueriesTest
{
    @Test
    public void testBuilder()
    {
        final TermSuggestionQuery query = TermSuggestionQuery.create( "queryName" ).
            field( "category" ).
            text( "value" ).
            analyzer( "analyzer" ).
            size( 1 ).
            maxEdits( 2 ).
            prefixLength( 3 ).
            minWordLength( 4 ).
            maxInspections( 5 ).
            minDocFreq( 6f ).
            maxTermFreq( 7f ).
            sort( TermSuggestionQuery.Sort.FREQUENCY ).
            suggestMode( TermSuggestionQuery.SuggestMode.ALWAYS ).
            stringDistance( TermSuggestionQuery.StringDistance.INTERNAL ).
            build();

        final SuggestionQueries queries = SuggestionQueries.create().
            add( query ).
            build();

        assertNotNull( queries );
        assertFalse( queries.isEmpty() );
        assertTrue( queries.contains( query ) );
    }

    @Test
    public void testEmpty()
    {
        final SuggestionQueries queries = SuggestionQueries.empty();

        assertTrue( queries.isEmpty() );
    }
}
