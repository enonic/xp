package com.enonic.xp.lib.node;

import java.util.Map;
import java.util.Optional;

import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.SuggestionQuery;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

@SuppressWarnings("unchecked")
final class QuerySuggestionParams
{
    QuerySuggestionParams()
    {
    }

    public SuggestionQueries getSuggestions( final Map<String, Object> suggestionsMap )
    {
        if ( suggestionsMap == null )
        {
            return SuggestionQueries.empty();
        }

        final SuggestionQueries.Builder suggestionQueries = SuggestionQueries.create();

        suggestionsMap.forEach( ( name, suggestionQueryMap ) -> {
            final SuggestionQuery suggestionQuery = suggestionQueryFromParams( name, (Map<String, Object>) suggestionQueryMap );
            if ( suggestionQuery != null )
            {
                suggestionQueries.add( suggestionQuery );
            }
        } );

        return suggestionQueries.build();
    }

    private SuggestionQuery suggestionQueryFromParams( final String name, final Map<String, Object> suggestionQueryMap )
    {
        if ( suggestionQueryMap.containsKey( "term" ) )
        {
            final String text = (String) suggestionQueryMap.get( "text" );
            final Map<String, Object> termParamsMap = (Map<String, Object>) suggestionQueryMap.get( "term" );
            final String field = (String) termParamsMap.get( "field" );
            final String analyzer = (String) termParamsMap.get( "analyzer" );
            final String sort = (String) termParamsMap.get( "sort" );
            final String suggestMode = (String) termParamsMap.get( "suggestMode" );
            final String stringDistance = (String) termParamsMap.get( "stringDistance" );
            final Integer size = getInteger( termParamsMap, "size" );
            final Integer maxEdits = getInteger( termParamsMap, "maxEdits" );
            final Integer prefixLength = getInteger( termParamsMap, "prefixLength" );
            final Integer minWordLength = getInteger( termParamsMap, "minWordLength" );
            final Integer maxInspections = getInteger( termParamsMap, "maxInspections" );
            final Float minDocFreq = getFloat( termParamsMap, "minDocFreq" );
            final Float maxTermFreq = getFloat( termParamsMap, "maxTermFreq" );

            return TermSuggestionQuery.create( name ).
                field( field ).
                text( text ).
                analyzer( analyzer ).
                size( size ).
                maxEdits( maxEdits ).
                prefixLength( prefixLength ).
                minWordLength( minWordLength ).
                maxInspections( maxInspections ).
                minDocFreq( minDocFreq ).
                maxTermFreq( maxTermFreq ).
                sort( TermSuggestionQuery.Sort.from( sort ) ).
                suggestMode( TermSuggestionQuery.SuggestMode.from( suggestMode ) ).
                stringDistance( TermSuggestionQuery.StringDistance.from( stringDistance ) ).
                build();
        }

        return null;
    }

    private Integer getInteger( final Map<String, Object> propertyMap, final String property )
    {
        return Optional.ofNullable( (Number) propertyMap.get( property ) ).
            map( Number::intValue ).
            orElse( null );
    }

    private Float getFloat( final Map<String, Object> propertyMap, final String property )
    {
        return Optional.ofNullable( (Number) propertyMap.get( property ) ).
            map( Number::floatValue ).
            orElse( null );
    }
}

