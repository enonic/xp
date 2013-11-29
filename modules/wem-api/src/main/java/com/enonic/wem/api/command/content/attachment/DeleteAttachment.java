package com.enonic.wem.api.command.content.attachment;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;

public final class DeleteAttachment
    extends Command<Boolean>
{
    private String attachmentName;

    private ContentPath contentPath;

    public DeleteAttachment attachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
        return this;
    }

    public DeleteAttachment contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath;
        return this;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteAttachment ) )
        {
            return false;
        }

        final DeleteAttachment that = (DeleteAttachment) o;
        return Objects.equal( this.attachmentName, that.attachmentName ) && Objects.equal( this.contentPath, that.contentPath );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.attachmentName, this.contentPath );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.attachmentName, "attachmentName cannot be null" );
        Preconditions.checkNotNull( this.contentPath, "contentPath cannot be null" );
    }
}
