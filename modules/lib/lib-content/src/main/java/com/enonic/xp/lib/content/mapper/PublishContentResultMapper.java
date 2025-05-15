package com.enonic.xp.lib.content.mapper;

import java.util.List;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PublishContentResultMapper
    implements MapSerializable
{
    private final PublishContentResult value;

    private final List<ContentPath> contentNotFound;

    public PublishContentResultMapper( final PublishContentResult value, final List<ContentPath> contentNotFound )
    {
        this.value = value;
        this.contentNotFound = contentNotFound;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        this.serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final PublishContentResult value )
    {
        serializeContentIds( gen, "pushedContents", value.getPushedContents() );
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

    private void serializeFailedContent( final MapGenerator gen, final String name, final ContentIds contents )
    {
        gen.array( name );
        for ( ContentId id : contents )
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
