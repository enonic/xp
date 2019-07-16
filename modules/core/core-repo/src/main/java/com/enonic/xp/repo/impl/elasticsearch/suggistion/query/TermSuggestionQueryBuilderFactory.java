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
        final String field = fieldNameResolver.resolve( suggestionQuery.getField(), IndexValueType.STRING );
        return new TermSuggestionBuilder( suggestionQuery.getName() ).
            field( field ).
            text( suggestionQuery.getText() ).
            size( suggestionQuery.getSize() ).
            analyzer( suggestionQuery.getAnalyzer() ).
            sort( suggestionQuery.getSort() != null ? suggestionQuery.getSort().value() : null ).
            suggestMode( suggestionQuery.getSuggestMode() != null ? suggestionQuery.getSuggestMode().value() : null ).
            maxEdits( suggestionQuery.getMaxEdits() ).
            prefixLength( suggestionQuery.getPrefixLength() ).
            minWordLength( suggestionQuery.getMinWordLength() ).
            maxInspections( suggestionQuery.getMaxInspections() ).
            minDocFreq( suggestionQuery.getMinDocFreq() ).
            maxTermFreq( suggestionQuery.getMaxTermFreq() ).
            stringDistance( suggestionQuery.getStringDistance() != null ? suggestionQuery.getStringDistance().value() : null );
    }
}
