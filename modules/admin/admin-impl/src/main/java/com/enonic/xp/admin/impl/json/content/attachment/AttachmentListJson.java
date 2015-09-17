package com.enonic.xp.admin.impl.json.content.attachment;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;

public class AttachmentListJson
{
    public static List<AttachmentJson> toJson( final Attachments attachments )
    {
        final ImmutableList.Builder<AttachmentJson> builder = new ImmutableList.Builder<>();
        for ( final Attachment attachment : attachments )
        {
            builder.add( new AttachmentJson( attachment ) );
        }
        return builder.build();
    }
}