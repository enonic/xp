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
        final TermSuggestionQuery query = TermSuggestionQuery.create( "query" ).
            field( "fieldName" ).
            build();

        final TermSuggestionBuilder suggestionBuilder = termSuggestionQueryBuilderFactory.create( query );

        Assert.assertNotNull( suggestionBuilder );
    }
}
