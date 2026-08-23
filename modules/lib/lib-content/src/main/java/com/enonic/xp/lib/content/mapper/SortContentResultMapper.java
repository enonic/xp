package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class SortContentResultMapper
    implements MapSerializable
{
    private final SortContentResult result;

    public SortContentResultMapper( final SortContentResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "content", new ContentMapper( result.getContent() ) );
        gen.array( "movedChildren" );
        if ( result.getMovedChildren() != null )
        {
            for ( final ContentId movedChild : result.getMovedChildren() )
            {
                gen.value( movedChild );
            }
        }
        gen.end();
    }
}
