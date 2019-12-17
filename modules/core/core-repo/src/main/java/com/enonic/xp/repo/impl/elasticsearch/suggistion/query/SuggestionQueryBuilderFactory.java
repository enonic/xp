package com.enonic.xp.repo.impl.elasticsearch.suggistion.query;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.suggest.SuggestionBuilder;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.SuggestionQuery;
import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class SuggestionQueryBuilderFactory
{
    private final QueryFieldNameResolver fieldNameResolver;

    public SuggestionQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        this.fieldNameResolver = fieldNameResolver;
    }

    public Set<SuggestionBuilder> create( final SuggestionQueries suggestionQueries )
    {
        return doCreate( suggestionQueries );
    }

    private Set<SuggestionBuilder> doCreate( final SuggestionQueries suggestionQueries )
    {
        Set<SuggestionBuilder> suggestionBuilders = new HashSet<>();

        for ( final SuggestionQuery suggestionQuery : suggestionQueries )
        {
            final SuggestionBuilder suggestionBuilder;

            if ( suggestionQuery instanceof TermSuggestionQuery )
            {
                suggestionBuilder =
                    new TermSuggestionQueryBuilderFactory( fieldNameResolver ).create( (TermSuggestionQuery) suggestionQuery );
            }
            else
            {
                throw new IllegalArgumentException( "Unexpected suggestion type: " + suggestionQuery.getClass() );
            }

            suggestionBuilders.add( suggestionBuilder );
        }

        return suggestionBuilders;
    }
}
