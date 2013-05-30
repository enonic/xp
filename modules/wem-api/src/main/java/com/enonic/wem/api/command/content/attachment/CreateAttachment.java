package com.enonic.wem.api.command.content.attachment;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;

public final class CreateAttachment
    extends Command<Void>
{
    private Attachment attachment;

    private ContentSelector contentSelector;

    public CreateAttachment attachment( final Attachment attachment )
    {
        this.attachment = attachment;
        return this;
    }

    public CreateAttachment contentSelector( final ContentSelector contentSelector )
    {
        this.contentSelector = contentSelector;
        return this;
    }

    public Attachment getAttachment()
    {
        return attachment;
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

        if ( !( o instanceof CreateAttachment ) )
        {
            return false;
        }

        final CreateAttachment that = (CreateAttachment) o;
        return Objects.equal( this.attachment, that.attachment ) && Objects.equal( this.contentSelector, that.contentSelector );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.attachment, this.contentSelector );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.attachment, "attachment cannot be null" );
        Preconditions.checkNotNull( this.contentSelector, "contentSelector cannot be null" );
    }

}
