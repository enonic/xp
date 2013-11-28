package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

public class GetContentByPaths
    extends Command<Contents>
{
    private final ContentPaths paths;

    public GetContentByPaths( final ContentPaths paths )
    {
        this.paths = paths;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( paths, "paths must be specified" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentByPaths ) )
        {
            return false;
        }

        final GetContentByPaths that = (GetContentByPaths) o;
        return Objects.equal( this.paths, that.paths );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.paths );
    }

    public ContentPaths getPaths()
    {
        return this.paths;
    }
}
