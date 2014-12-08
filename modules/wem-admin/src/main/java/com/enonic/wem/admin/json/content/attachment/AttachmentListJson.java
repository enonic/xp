package com.enonic.wem.admin.json.content.attachment;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;

public class AttachmentListJson
{
    private final ImmutableList<AttachmentJson> attachments;

    public AttachmentListJson( final Attachments attachments )
    {
        final ImmutableList.Builder<AttachmentJson> builder = new ImmutableList.Builder<>();
        for ( Attachment attachment : attachments )
        {
            builder.add( new AttachmentJson( attachment ) );
        }
        this.attachments = builder.build();
    }

    public static List<AttachmentJson> toJson( final Attachments attachments )
    {
        final ImmutableList.Builder<AttachmentJson> builder = new ImmutableList.Builder<>();
        for ( final Attachment attachment : attachments )
        {
            builder.add( new AttachmentJson( attachment ) );
        }
        return builder.build();
    }

    public List<AttachmentJson> getAttachments()
    {
        return attachments;
    }
}