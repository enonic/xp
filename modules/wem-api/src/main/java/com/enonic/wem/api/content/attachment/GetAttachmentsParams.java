package com.enonic.wem.api.content.attachment;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class GetAttachmentsParams
{
    private ContentId contentId;

    public GetAttachmentsParams contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof GetAttachmentsParams ) )
        {
            return false;
        }

        final GetAttachmentsParams that = (GetAttachmentsParams) o;
        return Objects.equals( contentId, that.contentId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentId );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
    }
}
