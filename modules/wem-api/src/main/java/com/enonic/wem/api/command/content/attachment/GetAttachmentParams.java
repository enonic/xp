package com.enonic.wem.api.command.content.attachment;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class GetAttachmentParams
{
    private String attachmentName;

    private ContentId contentId;

    public GetAttachmentParams attachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
        return this;
    }

    public GetAttachmentParams contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }


    public String getAttachmentName()
    {
        return attachmentName;
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final GetAttachmentParams that = (GetAttachmentParams) o;

        if ( attachmentName != null ? !attachmentName.equals( that.attachmentName ) : that.attachmentName != null )
        {
            return false;
        }
        if ( contentId != null ? !contentId.equals( that.contentId ) : that.contentId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = attachmentName != null ? attachmentName.hashCode() : 0;
        result = 31 * result + ( contentId != null ? contentId.hashCode() : 0 );
        return result;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
        Preconditions.checkNotNull( this.attachmentName, "attachmentName cannot be null" );
    }
}
