package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ContentIdsMapper
    implements MapSerializable
{

    private final ContentIds contentIds;

    public ContentIdsMapper( final ContentIds contentIds )
    {
        this.contentIds = contentIds;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "contentIds" );
        for ( final ContentId contentId : contentIds )
        {
            gen.value( contentId.toString() );
        }
        gen.end();
    }

}
