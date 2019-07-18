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

        final TermSuggestionBuilder builder = new TermSuggestionBuilder( suggestionQuery.getName() ).
            field( field ).
            text( suggestionQuery.getText() );

        final Integer size = suggestionQuery.getSize();
        final String analyzer = suggestionQuery.getAnalyzer();
        final TermSuggestionQuery.Sort sort = suggestionQuery.getSort();
        final TermSuggestionQuery.SuggestMode suggestMode = suggestionQuery.getSuggestMode();
        final Integer maxEdits = suggestionQuery.getMaxEdits();
        final Integer prefixLength = suggestionQuery.getPrefixLength();
        final Integer minWordLength = suggestionQuery.getMinWordLength();
        final Integer maxInspections = suggestionQuery.getMaxInspections();
        final Float minDocFreq = suggestionQuery.getMinDocFreq();
        final Float maxTermFreq = suggestionQuery.getMaxTermFreq();
        final TermSuggestionQuery.StringDistance stringDistance = suggestionQuery.getStringDistance();

        if (size != null) {
            builder.size( size );
        }
        if (analyzer != null) {
            builder.analyzer( analyzer );
        }
        if (sort != null) {
            builder.sort( sort.value() );
        }
        if (suggestMode != null) {
            builder.suggestMode( suggestMode.value() );
        }
        if (maxEdits != null) {
            builder.maxEdits( maxEdits );
        }
        if (prefixLength != null) {
            builder.prefixLength( prefixLength );
        }
        if (minWordLength != null) {
            builder.minWordLength( minWordLength );
        }
        if (maxInspections != null) {
            builder.maxInspections( maxInspections );
        }
        if (minDocFreq != null) {
            builder.minDocFreq( minDocFreq );
        }
        if (maxTermFreq != null) {
            builder.maxTermFreq( maxTermFreq );
        }
        if (stringDistance != null) {
            builder.stringDistance( stringDistance.value() );
        }
        return builder;
    }
}
