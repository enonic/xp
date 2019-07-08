package com.enonic.xp.lib.node;

import java.util.Map;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.SuggestionQuery;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

@SuppressWarnings("unchecked")
final class QuerySuggestionParams
{
    QuerySuggestionParams()
    {
    }

    public SuggestionQueries getSuggetions( final Map<String, Object> suggestionsMap )
    {
        if ( suggestionsMap == null )
        {
            return SuggestionQueries.empty();
        }

        final SuggestionQueries.Builder suggestionQueries = SuggestionQueries.create();
        for ( String name : suggestionsMap.keySet() )
        {
            final Map<String, Object> suggestionQueryMap = (Map<String, Object>) suggestionsMap.get( name );
            final SuggestionQuery suggestionQuery = suggestionQueryFromParams( name, suggestionQueryMap );
            if ( suggestionQuery != null )
            {
                suggestionQueries.add( suggestionQuery );
            }
        }

        return suggestionQueries.build();
    }

    private SuggestionQuery suggestionQueryFromParams( final String name, final Map<String, Object> suggestionQueryMap )
    {
        if ( suggestionQueryMap.containsKey( "term" ) )
        {
            final Map<String, Object> termParamsMap = (Map<String, Object>) suggestionQueryMap.get( "term" );
            return termsSuggestionFromParams( name, termParamsMap ).build();

        }

        return null;
    }

    private TermSuggestionQuery.Builder termsSuggestionFromParams( final String name, final Map<String, Object> paramsMap )
    {
        final String field = (String) paramsMap.get( "field" );

        return TermSuggestionQuery.create( name ).
            field( field );
    }
}

