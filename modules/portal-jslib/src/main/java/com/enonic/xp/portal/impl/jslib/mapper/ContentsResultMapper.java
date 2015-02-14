package com.enonic.xp.portal.impl.jslib.mapper;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Contents;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class ContentsResultMapper
    implements MapSerializable
{
    private final Contents contents;

    private final long total;

    private final Aggregations aggregations;

    public ContentsResultMapper( final Contents contents, final long total )
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = null;
    }

    public ContentsResultMapper( final Contents contents, final long total, final Aggregations aggregations )
    {
        this.contents = contents;
        this.total = total;
        this.aggregations = aggregations == null ? null : aggregations;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        serialize( gen, this.contents );
        serialize( gen, aggregations );
    }

    private void serialize( final MapGenerator gen, final Contents contents )
    {
        gen.array( "contents" );
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
}
