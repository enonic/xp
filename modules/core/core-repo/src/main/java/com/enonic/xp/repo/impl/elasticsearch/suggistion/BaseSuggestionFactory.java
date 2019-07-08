package com.enonic.xp.repo.impl.elasticsearch.suggistion;

import org.elasticsearch.search.suggest.Suggest;

import com.enonic.xp.suggester.Suggestion;
import com.enonic.xp.suggester.SuggestionEntry;
import com.enonic.xp.suggester.SuggestionOption;

abstract class BaseSuggestionFactory<OPTION extends Suggest.Suggestion.Entry.Option, ENTRY extends Suggest.Suggestion.Entry<OPTION>, SUGGESTION extends Suggest.Suggestion<ENTRY>>
{
    public Suggestion create( final SUGGESTION suggestion )
    {
        final Suggestion.Builder suggestionBuilder = initSuggestionBuilder( suggestion );

        suggestion.getEntries().forEach(
            entry -> suggestionBuilder.addSuggestionEntry( createSuggestionEntry( entry, initSuggestionEntryBuilder( entry ) ) ) );
        return suggestionBuilder.build();
    }

    private SuggestionEntry createSuggestionEntry( final ENTRY entry, final SuggestionEntry.Builder builder )
    {
        builder.
            text( entry.getText().string() ).
            length( entry.getLength() ).
            offset( entry.getOffset() );

        entry.getOptions().forEach(
            option -> builder.addSuggestionOption( createSuggestionOption( option, initSuggestionOptionBuilder( option ) ) ) );

        return builder.build();
    }

    private SuggestionOption createSuggestionOption( final OPTION option, final SuggestionOption.Builder builder )
    {
        return builder.
            text( option.getText().string() ).
            score( option.getScore() ).
            build();
    }

    abstract Suggestion.Builder initSuggestionBuilder( final SUGGESTION entry );

    abstract SuggestionEntry.Builder initSuggestionEntryBuilder( final ENTRY entry );

    abstract SuggestionOption.Builder initSuggestionOptionBuilder( final OPTION option );
}


