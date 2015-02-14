package com.enonic.xp.admin.impl.json.content.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.api.content.attachment.Attachment;

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
}