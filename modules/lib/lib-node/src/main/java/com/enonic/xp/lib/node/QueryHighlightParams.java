package com.enonic.xp.lib.node;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.enonic.xp.query.highlight.HighlightPropertySettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
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

        final Map<String, Object> propertiesMap = (Map<String, Object>) highlightMap.get( "properties" );

        if ( propertiesMap == null )
        {
            return HighlightQuery.empty();
        }

        propertiesMap.forEach( ( name, propertyMap ) -> {
            final HighlightQueryProperty highlightQueryProperty = highlightPropertyFromParams( name, (Map<String, Object>) propertyMap );
            highlightQuery.property( highlightQueryProperty );
        } );

        return highlightQuery.build();
    }

    private HighlightQueryProperty highlightPropertyFromParams( final String name, final Map<String, Object> propertyMap )
    {
        final HighlightPropertySettings settings = fillPropertySettings( HighlightPropertySettings.create(), propertyMap ).build();
        return HighlightQueryProperty.create( name ).settings( settings ).build();
    }

    private HighlightQuerySettings.Builder fillQuerySettings( final HighlightQuerySettings.Builder builder,
                                                              final Map<String, Object> propertyMap )
    {
        fillPropertySettings( builder, propertyMap );

        return builder.encoder( Encoder.from( (String) propertyMap.get( "encoder" ) ) ).
            tagsSchema( TagsSchema.from( (String) propertyMap.get( "tagsSchema" ) ) );

    }

    private HighlightPropertySettings.Builder fillPropertySettings( final HighlightPropertySettings.Builder builder,
                                                                    final Map<String, Object> propertyMap )
    {
        return builder.fragmenter( Fragmenter.from( (String) propertyMap.get( "fragmenter" ) ) ).
            fragmentSize( getInteger( propertyMap, "fragmentSize" ) ).
            noMatchSize( getInteger( propertyMap, "noMatchSize" ) ).
            numOfFragments( getInteger( propertyMap, "numberOfFragments" ) ).
            order( Order.from( (String) propertyMap.get( "order" ) ) ).
            addPreTags(
                propertyMap.get( "preTag" ) == null ? Collections.emptyList() : Collections.singletonList( propertyMap.get( "preTag" ) ) ).
            addPostTags( propertyMap.get( "postTag" ) == null
                             ? Collections.emptyList()
                             : Collections.singletonList( propertyMap.get( "postTag" ) ) ).
            requireFieldMatch( (Boolean) propertyMap.get( "requireFieldMatch" ) );
    }

    private Integer getInteger( final Map<String, Object> propertyMap, final String property )
    {
        return Optional.ofNullable( (Number) propertyMap.get( property ) ).
            map( Number::intValue ).
            orElse( null );
    }
}

