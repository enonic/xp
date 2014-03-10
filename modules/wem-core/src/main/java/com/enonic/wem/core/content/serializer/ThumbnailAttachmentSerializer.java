package com.enonic.wem.core.content.serializer;


import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.entity.Attachment;


public class ThumbnailAttachmentSerializer
{
    public static final String THUMB_NAME = "_thumb.png";

    public static Attachment toAttachment( final Thumbnail thumbnail )
    {
        if ( thumbnail == null )
        {
            return null;
        }

        final Attachment attachment = Attachment.newAttachment().
            size( thumbnail.getSize() ).
            name( THUMB_NAME ).
            mimeType( thumbnail.getMimeType() ).
            blobKey( thumbnail.getBlobKey() ).
            build();

        return attachment;
    }

    public static Thumbnail toThumbnail( final Attachment attachment )
    {
        if ( attachment == null )
        {
            return null;
        }

        final BlobKey blobKey = attachment.blobKey();
        final String mimeType = attachment.mimeType();
        return Thumbnail.from( blobKey, mimeType, attachment.size() );
    }
}
