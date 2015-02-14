package com.enonic.xp.portal.impl.jslib.mapper;

import com.enonic.xp.core.content.attachment.Attachment;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class AttachmentMapper
    implements MapSerializable
{
    private final Attachment value;

    public AttachmentMapper( final Attachment value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final Attachment value )
    {
        gen.value( "binaryReference", value.getBinaryReference().toString() );
        gen.value( "size", value.getSize() );
        gen.value( "mimeType", value.getMimeType() );
    }

}
