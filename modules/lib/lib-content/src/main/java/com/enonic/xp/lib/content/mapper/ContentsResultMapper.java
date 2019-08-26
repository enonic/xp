package com.enonic.xp.lib.content.mapper;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentsResultMapper
    implements MapSerializable
{
    private final Contents contents;

    private final long total;

    private final Aggregations aggregations;

    private final ImmutableMap<ContentId, HighlightedFields> highlight;

    public ContentsResultMapper( final Contents contents, final long total )
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = null;
        this.highlight = null;
    }

    public ContentsResultMapper( final Contents contents, final long total, final Aggregations aggregations,
                                 final ImmutableMap<ContentId, HighlightedFields> highlight )
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = aggregations;
        this.highlight = highlight;
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
            new ContentMapper( content ).serialize( gen );
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

    private void serialize( final MapGenerator gen, ImmutableMap<ContentId, HighlightedFields> highlight )
    {
        if ( highlight != null )
        {
            gen.map( "highlight" );
            new HighlightMapper( highlight ).serialize( gen );
            gen.end();
        }
    }
}
