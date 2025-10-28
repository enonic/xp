package com.enonic.xp.query.suggestion;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.suggester.TermSuggestionQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TermSuggestionQueryTest
{
    @Test
    void testBuilder()
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

        assertEquals( "queryName", query.getName() );
        assertEquals( "category", query.getField() );
        assertEquals( "value", query.getText() );
        assertEquals( "analyzer", query.getAnalyzer() );
        assertEquals( (Integer) 1, query.getSize() );
        assertEquals( (Integer) 2, query.getMaxEdits() );
        assertEquals( (Integer) 3, query.getPrefixLength() );
        assertEquals( (Integer) 4, query.getMinWordLength() );
        assertEquals( (Integer) 5, query.getMaxInspections() );
        assertEquals( 6F, query.getMinDocFreq(), 0.0 );
        assertEquals( 7F, query.getMaxTermFreq(), 0.0 );
        assertEquals( TermSuggestionQuery.Sort.FREQUENCY, query.getSort() );
        assertEquals( TermSuggestionQuery.SuggestMode.ALWAYS, query.getSuggestMode() );
        assertEquals( TermSuggestionQuery.StringDistance.INTERNAL, query.getStringDistance() );
    }

}
