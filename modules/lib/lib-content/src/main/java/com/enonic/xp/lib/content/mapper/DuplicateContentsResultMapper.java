package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class DuplicateContentsResultMapper
    implements MapSerializable
{
    private final DuplicateContentsResult duplicateContentsResult;

    public DuplicateContentsResultMapper( final DuplicateContentsResult duplicateContentsResult )
    {
        this.duplicateContentsResult = duplicateContentsResult;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "contentName", duplicateContentsResult.getContentName() );
        gen.value( "sourceContentPath", duplicateContentsResult.getSourceContentPath() );
        serializeDuplicatedContents( gen );
    }

    private void serializeDuplicatedContents( final MapGenerator gen )
    {
        gen.array( "duplicatedContents" );
        for ( ContentId contentId : duplicateContentsResult.getDuplicatedContents() )
        {
            gen.value( contentId.toString() );
        }
        gen.end();
    }
}
