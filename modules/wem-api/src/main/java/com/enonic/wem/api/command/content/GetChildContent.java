package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

public final class GetChildContent
    extends Command<Contents>
{
    private ContentPath parentPath;

    public ContentPath getParentPath()
    {
        return this.parentPath;
    }

    public GetChildContent parentPath( final ContentPath parentPath )
    {
        this.parentPath = parentPath;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetChildContent ) )
        {
            return false;
        }

        final GetChildContent that = (GetChildContent) o;
        return Objects.equal( this.parentPath, that.parentPath );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.parentPath );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.parentPath, "parentPath cannot be null" );
    }
}
