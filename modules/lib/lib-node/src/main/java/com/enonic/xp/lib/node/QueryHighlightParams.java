package com.enonic.xp.lib.node;

import java.util.Map;

import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;

@SuppressWarnings("unchecked")
final class QueryHighlightParams
{
    QueryHighlightParams()
    {
    }

    public HighlightQuery getHighlightQuery( final Map<String, Object> highlightMap )
    {
        if ( highlightMap == null )
        {
            return HighlightQuery.empty();
        }

        final HighlightQuery.Builder highlightQuery = HighlightQuery.create();
        final Map<String, Object> fieldsMap = (Map<String, Object>) highlightMap.get( "fields" );

        if ( fieldsMap == null )
        {
            return HighlightQuery.empty();
        }

        for ( String name : fieldsMap.keySet() )
        {
            final Map<String, Object> fieldMap = (Map<String, Object>) fieldsMap.get( name );
            final HighlightQueryField highlightQueryField = highlightFieldFromParams( name, fieldMap );

            highlightQuery.field( highlightQueryField );
        }

        return highlightQuery.build();
    }

    private HighlightQueryField highlightFieldFromParams( final String name, final Map<String, Object> fieldMap )
    {
        return HighlightQueryField.create( name ).build();
    }
}

