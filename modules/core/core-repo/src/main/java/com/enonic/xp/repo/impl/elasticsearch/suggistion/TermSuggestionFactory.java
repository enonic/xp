package com.enonic.xp.repo.impl.elasticsearch.suggistion;

import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry.Option;

import com.enonic.xp.suggester.TermSuggestionEntry;
import com.enonic.xp.suggester.TermSuggestionOption;

final class TermSuggestionFactory
    extends BaseSuggestionFactory<Option, Entry, TermSuggestion>
{
    @Override
    public com.enonic.xp.suggester.TermSuggestion create( final TermSuggestion termSuggestion )
    {
        return (com.enonic.xp.suggester.TermSuggestion) super.create( termSuggestion );
    }

    @Override
    com.enonic.xp.suggester.TermSuggestion.Builder initSuggestionBuilder( final TermSuggestion suggestion )
    {
        return com.enonic.xp.suggester.TermSuggestion.create( suggestion.getName() );
    }

    @Override
    TermSuggestionEntry.Builder initSuggestionEntryBuilder( final Entry entry )
    {
        return TermSuggestionEntry.create();
    }

    @Override
    TermSuggestionOption.Builder initSuggestionOptionBuilder( final Option option )
    {
        return TermSuggestionOption.create().
            freq( option.getFreq() );
    }
}
