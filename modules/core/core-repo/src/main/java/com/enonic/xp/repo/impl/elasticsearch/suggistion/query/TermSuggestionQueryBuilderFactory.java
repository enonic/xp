package com.enonic.xp.repo.impl.elasticsearch.suggistion.query;

import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import com.enonic.xp.query.suggester.TermSuggestionQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class TermSuggestionQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public TermSuggestionQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public TermSuggestionBuilder create( final TermSuggestionQuery suggestionQuery )
    {
        final TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder( suggestionQuery.getName() );

        final String field = fieldNameResolver.resolve( suggestionQuery.getField(), IndexValueType.STRING );

        termSuggestionBuilder.field( field );

        return termSuggestionBuilder;
    }
}
