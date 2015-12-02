package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class AttachmentsMapper
    implements MapSerializable
{

    private final Attachments attachments;

    public AttachmentsMapper( final Attachments attachments )
    {
        this.attachments = attachments;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Attachment attachment : attachments )
        {
            gen.map( attachment.getName() );
            serializeAttachment( gen, attachment );
            gen.end();
        }
    }

    private void serializeAttachment( final MapGenerator gen, final Attachment attachment )
    {
        gen.value( "name", attachment.getName() );
        gen.value( "label", attachment.getLabel() );
        gen.value( "size", attachment.getSize() );
        gen.value( "mimeType", attachment.getMimeType() );
    }
}
