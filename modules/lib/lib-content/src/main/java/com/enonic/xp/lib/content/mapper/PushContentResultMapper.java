package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PushContentResultMapper
    implements MapSerializable
{
    private final PushContentsResult value;

    public PushContentResultMapper( final PushContentsResult value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        this.serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final PushContentsResult value )
    {
        serializeContentIds( gen, "pushedContents", value.getPushedContents() );
        serializeContentIds( gen, "deletedContents", value.getDeletedContents() );
        serializeContentIds( gen, "failedContents", value.getFailedContents() );
    }

    private static void serializeContentIds( final MapGenerator gen, final String name, final Contents contents )
    {
        gen.array( name );
        for ( ContentId id : contents.getIds() )
        {
            gen.value( id.toString() );
        }
        gen.end();
    }

}
