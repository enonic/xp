package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.versioning.ContentVersionHistory;

public final class GetContentVersionHistory
    extends Command<ContentVersionHistory>
{
    private ContentId contentId;

    public ContentId getContentId()
    {
        return contentId;
    }

    public GetContentVersionHistory contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentVersionHistory ) )
        {
            return false;
        }

        final GetContentVersionHistory that = (GetContentVersionHistory) o;
        return Objects.equal( this.contentId, that.contentId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "ContentId cannot be null" );
    }
}
