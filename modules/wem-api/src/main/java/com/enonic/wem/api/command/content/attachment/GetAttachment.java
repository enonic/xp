package com.enonic.wem.api.command.content.attachment;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;

public final class GetAttachment
    extends Command<Attachment>
{
    private String attachmentName;

    private ContentSelector contentSelector;

    public GetAttachment attachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
        return this;
    }

    public GetAttachment contentSelector( final ContentSelector contentSelector )
    {
        this.contentSelector = contentSelector;
        return this;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public ContentSelector getContentSelector()
    {
        return contentSelector;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetAttachment ) )
        {
            return false;
        }

        final GetAttachment that = (GetAttachment) o;
        return Objects.equal( this.attachmentName, that.attachmentName ) && Objects.equal( this.contentSelector, that.contentSelector );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.attachmentName, this.contentSelector );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.attachmentName, "attachmentName cannot be null" );
        Preconditions.checkNotNull( this.contentSelector, "contentSelector cannot be null" );
    }

}
