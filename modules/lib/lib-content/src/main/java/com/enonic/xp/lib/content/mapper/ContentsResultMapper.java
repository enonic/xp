package com.enonic.xp.lib.content.mapper;

import java.util.Map;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.sortvalues.SortValuesProperty;

public final class ContentsResultMapper
    implements MapSerializable
{
    private final Contents contents;

    private final long total;

    private final Aggregations aggregations;

    private final Map<ContentId, HighlightedProperties> highlight;

    private final Map<ContentId, SortValuesProperty> sortValues;

    private final Map<ContentId, Float> scoreValues;

    public ContentsResultMapper( final Contents contents, final long total )
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = null;
        this.highlight = null;
        this.sortValues = null;
        this.scoreValues = null;
    }

    public ContentsResultMapper( final Contents contents, final long total, final Aggregations aggregations,
                                 final Map<ContentId, HighlightedProperties> highlight,
                                 final Map<ContentId, SortValuesProperty> sortValues,
                                 final Map<ContentId, Float> scoreValues)
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = aggregations;
        this.highlight = highlight;
        this.sortValues = sortValues;
        this.scoreValues = scoreValues;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.contents.getSize() );
        serialize( gen, this.contents );
        serialize( gen, aggregations );
        serialize( gen, highlight );
    }

    private void serialize( final MapGenerator gen, final Contents contents )
    {
        gen.array( "hits" );
        for ( Content content : contents )
        {
            gen.map();
            final SortValuesProperty sort = sortValues != null ? sortValues.get( content.getId() ) : null;
            final Float score = scoreValues != null ? scoreValues.get( content.getId() ) : null;

            new ContentMapper( content, sort, score ).serialize( gen );
            gen.end();
        }
        gen.end();
    }

    private void serialize( final MapGenerator gen, final Aggregations aggregations )
    {
        if ( aggregations != null )
        {
            gen.map( "aggregations" );
            new AggregationMapper( aggregations ).serialize( gen );
            gen.end();
        }
    }

    private void serialize( final MapGenerator gen, Map<ContentId, HighlightedProperties> highlight )
    {
        if ( highlight != null )
        {
            gen.map( "highlight" );
            new HighlightMapper( highlight ).serialize( gen );
            gen.end();
        }
    }
}
