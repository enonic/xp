package com.enonic.wem.api.command.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;

public final class ContentGetCommands
{
    public GetContentById byId( final ContentId id )
    {
        return new GetContentById( id );
    }

    public GetContentByIds byIds( final ContentIds ids )
    {
        return new GetContentByIds( ids );
    }

    public GetContentByPath byPath( final ContentPath path )
    {
        return new GetContentByPath( path );
    }

    public GetContentByPaths byPaths( final ContentPaths paths )
    {
        return new GetContentByPaths( paths );
    }
}