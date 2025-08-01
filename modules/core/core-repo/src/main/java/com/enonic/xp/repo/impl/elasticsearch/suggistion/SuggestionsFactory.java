package com.enonic.xp.repo.impl.elasticsearch.suggistion;

import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.term.TermSuggestion;

import com.enonic.xp.suggester.Suggestions;


public class SuggestionsFactory
{
    private static final TermSuggestionFactory TERM_SUGGESTION_FACTORY = new TermSuggestionFactory();

    public static Suggestions create( final Suggest suggest )
    {
        return doCreate( suggest );
    }

    private static Suggestions doCreate( final Suggest suggest )
    {
        if ( suggest == null )
        {
            return Suggestions.empty();
        }

        Suggestions.Builder suggestionsBuilder = Suggestions.create();

        for ( final Suggest.Suggestion suggestion : suggest )
        {
            if ( suggestion instanceof TermSuggestion )
            {
                suggestionsBuilder.add( TERM_SUGGESTION_FACTORY.create( (TermSuggestion) suggestion ) );
            }
            else
            {
                throw new IllegalArgumentException( "Suggestion translator for " + suggest.getClass().getName() + " not implemented" );
            }
        }

        return suggestionsBuilder.build();
    }

}


