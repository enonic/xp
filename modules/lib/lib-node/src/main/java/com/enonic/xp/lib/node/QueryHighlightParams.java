package com.enonic.xp.lib.node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.enonic.xp.query.highlight.HighlightFieldSettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryField;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;

@SuppressWarnings("unchecked")
final class QueryHighlightParams
{
    QueryHighlightParams()
    {
    }

    HighlightQuery getHighlightQuery( final Map<String, Object> highlightMap )
    {
        if ( highlightMap == null )
        {
            return HighlightQuery.empty();
        }

        final HighlightQuerySettings settings = fillQuerySettings( HighlightQuerySettings.create(), highlightMap ).build();
        final HighlightQuery.Builder highlightQuery = HighlightQuery.create().settings( settings );

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
        final HighlightFieldSettings settings = fillFieldSettings( HighlightFieldSettings.create(), fieldMap ).build();
        return HighlightQueryField.create( name ).settings( settings ).build();
    }

    private HighlightQuerySettings.Builder fillQuerySettings( final HighlightQuerySettings.Builder builder,
                                                              final Map<String, Object> fieldMap )
    {
        fillFieldSettings( builder, fieldMap );

        return builder.encoder( Encoder.from( (String) fieldMap.get( "encoder" ) ) ).
            tagsSchema( TagsSchema.from( (String) fieldMap.get( "tagsSchema" ) ) );

    }

    private HighlightFieldSettings.Builder fillFieldSettings( final HighlightFieldSettings.Builder builder,
                                                              final Map<String, Object> fieldMap )
    {
        return builder.fragmenter( Fragmenter.from( (String) fieldMap.get( "fragmenter" ) ) ).
            fragmentSize( (Integer) fieldMap.get( "fragmentSize" ) ).
            noMatchSize( (Integer) fieldMap.get( "noMatchSize" ) ).
            numOfFragments( (Integer) fieldMap.get( "numberOfFragments" ) ).
            order( Order.from( (String) fieldMap.get( "order" ) ) ).
            addPreTags(
                fieldMap.get( "preTag" ) == null ? Collections.emptyList() : Collections.singletonList( fieldMap.get( "preTag" ) ) ).
            addPostTags(
                fieldMap.get( "postTag" ) == null ? Collections.emptyList() : Collections.singletonList( fieldMap.get( "postTag" ) ) ).
            requireFieldMatch( (Boolean) fieldMap.get( "requireFieldMatch" ) );
    }

}

