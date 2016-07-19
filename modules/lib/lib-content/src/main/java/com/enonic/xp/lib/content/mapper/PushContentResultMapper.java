package com.enonic.xp.lib.content.mapper;

import java.util.List;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PushContentResultMapper
    implements MapSerializable
{
    private final PushContentsResult value;

    private final List<ContentPath> contentNotFound;

    public PushContentResultMapper( final PushContentsResult value, final List<ContentPath> contentNotFound )
    {
        this.value = value;
        this.contentNotFound = contentNotFound;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        this.serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final PushContentsResult value )
    {
        serializeContentIds( gen, "pushedContents", value.getPushedContents() );
        serializeContentIds( gen, "deletedContents", value.getDeletedContents() );
        serializeFailedContent( gen, "failedContents", value.getFailedContents() );
    }

    private void serializeContentIds( final MapGenerator gen, final String name, final ContentIds contents )
    {
        gen.array( name );
        for ( ContentId id : contents )
        {
            gen.value( id.toString() );
        }
        gen.end();
    }

    private void serializeFailedContent( final MapGenerator gen, final String name, final ContentIds contentIds )
    {
        gen.array( name );
        for ( ContentId id : contentIds )
        {
            gen.value( id.toString() );
        }
        for ( ContentPath path : this.contentNotFound )
        {
            gen.value( path.toString() );
        }
        gen.end();
    }
}
