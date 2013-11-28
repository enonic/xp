package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;

public class GetContentByPath
    extends Command<Content>
{
    private final ContentPath path;

    public GetContentByPath( final ContentPath path )
    {
        this.path = path;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( path, "path must be specified" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final GetContentByPath that = (GetContentByPath) o;

        if ( !path.equals( that.path ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }

    public ContentPath getPath()
    {
        return this.path;
    }
}
