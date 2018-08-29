package com.enonic.xp.admin.impl.json.content.attachment;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.attachment.Attachment;

@SuppressWarnings("UnusedDeclaration")
public class AttachmentJson
{
    private final Attachment attachment;

    public AttachmentJson( final Attachment attachment )
    {
        this.attachment = attachment;
    }

    public String getName()
    {
        return this.attachment.getName();
    }

    public String getLabel()
    {
        return this.attachment.getLabel();
    }

    public String getMimeType()
    {
        return this.attachment.getMimeType();
    }

    public long getSize()
    {
        return this.attachment.getSize();
    }

    @JsonIgnore
    public Attachment getAttachment()
    {
        return attachment;
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
        final AttachmentJson that = (AttachmentJson) o;
        return Objects.equals( attachment, that.attachment );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( attachment );
    }
}