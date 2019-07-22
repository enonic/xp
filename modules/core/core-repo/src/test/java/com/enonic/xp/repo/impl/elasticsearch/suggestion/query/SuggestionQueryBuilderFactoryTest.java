package com.enonic.xp.repo.impl.elasticsearch.suggestion.query;

import java.util.Set;

import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.suggistion.query.SuggestionQueryBuilderFactory;

public class SuggestionQueryBuilderFactoryTest
{
    private SuggestionQueryBuilderFactory suggestionQueryBuilderFactory;

    @Before
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

        final Set<SuggestBuilder.SuggestionBuilder> suggestionBuilder =
            suggestionQueryBuilderFactory.create( SuggestionQueries.create().add( query ).build() );

        Assert.assertNotNull( suggestionBuilder );
        Assert.assertEquals( 1, suggestionBuilder.size() );
        Assert.assertEquals( TermSuggestionBuilder.class, suggestionBuilder.toArray()[0].getClass() );

    }

}
