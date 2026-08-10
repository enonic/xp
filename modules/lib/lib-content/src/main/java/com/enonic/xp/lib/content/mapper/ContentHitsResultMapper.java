package com.enonic.xp.lib.content.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.index.FieldValues;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * The lightweight result shapes of a content query: hits carrying ids, or the requested index-field values, instead of full contents.
 */
public final class ContentHitsResultMapper
    implements MapSerializable
{
    public enum Shape
    {
        IDS, FIELDS
    }

    private final FindContentIdsByQueryResult result;

    private final Shape shape;

    private final List<String> returns;

    public ContentHitsResultMapper( final FindContentIdsByQueryResult result, final Shape shape )
    {
        this( result, shape, List.of() );
    }

    public ContentHitsResultMapper( final FindContentIdsByQueryResult result, final Shape shape, final List<String> returns )
    {
        this.result = result;
        this.shape = shape;
        this.returns = returns;
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

            final Float score = result.getScore() != null ? result.getScore().get( contentId ) : null;
            gen.value( "score", score == null || Float.isNaN( score ) ? 0.0 : score );

            if ( shape == Shape.FIELDS )
            {
                serializeFields( gen, result.getFields().getOrDefault( contentId, FieldValues.empty() ), this.returns );
            }

            gen.end();
        }
        gen.end();

        serialize( gen, result.getAggregations() );
        serialize( gen, result.getHighlight() );
    }

    /**
     * Writes the requested fields under the very names they were requested by. The values are keyed by index path and therefore
     * lowercase, so walking the request rather than the answer is what lets a hit read back the way it was asked for.
     */
    private static void serializeFields( final MapGenerator gen, final FieldValues fields, final List<String> returns )
    {
        if ( fields.isEmpty() || returns.isEmpty() )
        {
            return;
        }
        gen.map( "fields" );
        for ( final String name : returns )
        {
            final List<Object> values = fields.getValues( name );
            if ( values.isEmpty() )
            {
                continue;
            }
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
