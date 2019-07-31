package com.enonic.xp.repo.impl.elasticsearch.suggestion.query;

import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.suggistion.query.TermSuggestionQueryBuilderFactory;

public class TermSuggestionQueryBuilderFactoryTest
{
    private TermSuggestionQueryBuilderFactory termSuggestionQueryBuilderFactory;

    @Before
    public void init()
    {
        termSuggestionQueryBuilderFactory = new TermSuggestionQueryBuilderFactory( new SearchQueryFieldNameResolver() );
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

        final TermSuggestionBuilder suggestionBuilder = termSuggestionQueryBuilderFactory.create( query );

        Assert.assertNotNull( suggestionBuilder );
    }
}
