package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentVersionsResultMapper
    implements MapSerializable
{
    private final ContentVersions contentVersions;

    private final long count;

    private final long total;

    public ContentVersionsResultMapper( final FindContentVersionsResult findContentVersionsResult )
    {
        this.contentVersions = findContentVersionsResult.getContentVersions();
        this.count = findContentVersionsResult.getHits();
        this.total = findContentVersionsResult.getTotalHits();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.count );
        serialize( gen, this.contentVersions );
    }

    private void serialize( final MapGenerator gen, final ContentVersions contentVersions )
    {
        gen.array( "hits" );
        for ( ContentVersion contentVersion : contentVersions )
        {
            gen.map();
            new ContentVersionMapper( contentVersion ).
                serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
