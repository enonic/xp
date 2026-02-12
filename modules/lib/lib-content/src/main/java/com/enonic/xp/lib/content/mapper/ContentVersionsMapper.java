package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentVersionsMapper
    implements MapSerializable
{
    private final GetContentVersionsResult result;

    public ContentVersionsMapper( final GetContentVersionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", result.getTotalHits() );
        gen.value( "count", result.getContentVersions().getSize() );
        gen.value( "cursor", result.getCursor() );
        serializeHits( gen );
    }

    private void serializeHits( final MapGenerator gen )
    {
        gen.array( "hits" );
        for ( ContentVersion version : result.getContentVersions() )
        {
            gen.map();
            new ContentVersionMapper( version ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
