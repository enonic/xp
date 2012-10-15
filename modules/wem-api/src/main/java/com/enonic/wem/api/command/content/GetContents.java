package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;

public final class GetContents
    extends Command<Contents>
{
    private ContentPaths paths;

    public ContentPaths getPaths()
    {
        return this.paths;
    }

    public GetContents paths( final ContentPaths paths )
    {
        this.paths = paths;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContents ) )
        {
            return false;
        }

        final GetContents that = (GetContents) o;
        return Objects.equal( this.paths, that.paths );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.paths );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.paths, "Content paths cannot be null" );
    }
}
