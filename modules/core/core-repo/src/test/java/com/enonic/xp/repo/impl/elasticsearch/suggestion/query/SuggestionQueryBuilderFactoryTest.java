package com.enonic.xp.repo.impl.elasticsearch.suggestion.query;

import java.util.Set;

import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.suggistion.query.SuggestionQueryBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SuggestionQueryBuilderFactoryTest
{
    private SuggestionQueryBuilderFactory suggestionQueryBuilderFactory;

    @BeforeEach
    public void init()
    {
        suggestionQueryBuilderFactory = new SuggestionQueryBuilderFactory( new SearchQueryFieldNameResolver() );
    }

    @Test
    public void create()
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

        final Set<SuggestionBuilder> suggestionBuilder =
            suggestionQueryBuilderFactory.create( SuggestionQueries.create().add( query ).build() );

        assertNotNull( suggestionBuilder );
        assertEquals( 1, suggestionBuilder.size() );
        assertEquals( TermSuggestionBuilder.class, suggestionBuilder.toArray()[0].getClass() );

    }

}
