package com.enonic.xp.lib.content.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.index.FieldValues;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * The lightweight result shapes of a content query: hits carrying ids, paths or requested index-field values instead of full contents.
 */
public final class ContentHitsResultMapper
    implements MapSerializable
{
    public enum Shape
    {
        IDS, PATHS, FIELDS
    }

    private final FindContentIdsByQueryResult result;

    private final Shape shape;

    public ContentHitsResultMapper( final FindContentIdsByQueryResult result, final Shape shape )
    {
        this.result = result;
        this.shape = shape;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", result.getTotalHits() );
        gen.value( "count", result.getContentIds().getSize() );

        gen.array( "hits" );
        for ( final ContentId contentId : result.getContentIds() )
        {
            gen.map();
            gen.value( "id", contentId );

            if ( shape == Shape.PATHS )
            {
                result.getFields()
                    .getOrDefault( contentId, FieldValues.empty() )
                    .getSingleValue( NodeIndexPath.PATH )
                    .ifPresent( path -> gen.value( "path", path ) );
            }
            else
            {
                final Float score = result.getScore() != null ? result.getScore().get( contentId ) : null;
                gen.value( "score", score == null || Float.isNaN( score ) ? 0.0 : score );
            }

            if ( shape == Shape.FIELDS )
            {
                serializeFields( gen, result.getFields().getOrDefault( contentId, FieldValues.empty() ) );
            }

            gen.end();
        }
        gen.end();

        serialize( gen, result.getAggregations() );
        serialize( gen, result.getHighlight() );
    }

    private static void serializeFields( final MapGenerator gen, final FieldValues fields )
    {
        if ( fields.isEmpty() )
        {
            return;
        }
        gen.map( "fields" );
        for ( final String field : fields.getFields() )
        {
            // the index keys are lowercase; hits show the field under the name a content shows it by
            final String name = ContentQuery.SUPPORTED_RETURN_FIELDS.getOrDefault( IndexPath.from( field ), field );
            final List<Object> values = fields.getValues( field );
            if ( values.size() == 1 )
            {
                // single values come back as scalars, like property values do everywhere else in the JS API
                gen.value( name, values.get( 0 ) );
            }
            else
            {
                gen.array( name );
                for ( final Object value : values )
                {
                    gen.value( value );
                }
                gen.end();
            }
        }
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final Aggregations aggregations )
    {
        if ( aggregations != null )
        {
            gen.map( "aggregations" );
            new AggregationMapper( aggregations ).serialize( gen );
            gen.end();
        }
    }

    private static void serialize( final MapGenerator gen, final Map<ContentId, HighlightedProperties> highlight )
    {
        if ( highlight != null && !highlight.isEmpty() )
        {
            gen.map( "highlight" );
            new HighlightMapper( highlight ).serialize( gen );
            gen.end();
        }
    }
}
