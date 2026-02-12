package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentVersionMapper
    implements MapSerializable
{
    private final ContentVersion version;

    public ContentVersionMapper( final ContentVersion version )
    {
        this.version = version;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
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
    }
}
