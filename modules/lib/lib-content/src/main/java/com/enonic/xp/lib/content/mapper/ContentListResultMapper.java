package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentListEntry;
import com.enonic.xp.content.ListContentsByParentResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentListResultMapper
    implements MapSerializable
{
    private final ListContentsByParentResult result;

    public ContentListResultMapper( final ListContentsByParentResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "count", result.getSize() );
        gen.array( "hits" );
        for ( final ContentListEntry entry : result.getEntries() )
        {
            gen.map();
            gen.value( "id", entry.id() );
            gen.value( "path", entry.path() );
            gen.end();
        }
        gen.end();
    }
}
