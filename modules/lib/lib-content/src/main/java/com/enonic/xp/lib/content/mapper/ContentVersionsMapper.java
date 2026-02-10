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
            gen.value( "versionId", version.versionId() );
            gen.value( "contentId", version.contentId() );
            gen.value( "path", version.path() );
            gen.value( "timestamp", version.timestamp() );
            gen.value( "comment", version.comment() );

            if ( !version.actions().isEmpty() )
            {
                gen.array( "actions" );
                for ( ContentVersion.Action action : version.actions() )
                {
                    gen.map();
                    gen.value( "operation", action.operation() );
                    gen.value( "user", action.user() );
                    gen.value( "opTime", action.opTime() );
                    gen.end();
                }
                gen.end();
            }

            gen.end();
        }
        gen.end();
    }
}
