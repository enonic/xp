package com.enonic.wem.api.command.content.attachment;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachments;

public final class GetAttachments
    extends Command<Attachments>
{
    private ContentId contentId;

    private boolean includeThumbnail = false;

    public GetAttachments contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public boolean isIncludeThumbnail()
    {
        return includeThumbnail;
    }

    public GetAttachments setIncludeThumbnail( final boolean includeThumbnail )
    {
        this.includeThumbnail = includeThumbnail;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof GetAttachments ) )
        {
            return false;
        }

        final GetAttachments that = (GetAttachments) o;
        return Objects.equals( contentId, that.contentId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentId );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
    }
}
