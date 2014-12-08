package com.enonic.wem.core.content;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.node.Attachments;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;

public class ContentAttachmentNodeTranslator
{

    public Attachments toNodeAttachments( final com.enonic.wem.api.content.attachment.Attachments contentAttachments )
    {
        if ( contentAttachments == null )
        {
            return null;
        }
        else if ( contentAttachments.isEmpty() )
        {
            return Attachments.empty();
        }

        final Attachments.Builder attachmentsBuilder = Attachments.builder();

        for ( final Attachment contentAttachment : contentAttachments )
        {
            final com.enonic.wem.api.node.Attachment attachment = doTranslateToNodeAttachment( contentAttachment );

            attachmentsBuilder.add( attachment );
        }

        return attachmentsBuilder.build();
    }

    private com.enonic.wem.api.node.Attachment doTranslateToNodeAttachment( final Attachment contentAttachment )
    {
        return com.enonic.wem.api.node.Attachment.newAttachment().blobKey( contentAttachment.getBlobKey() ).
            mimeType( contentAttachment.getMimeType() ).
            size( contentAttachment.getSize() ).
            name( contentAttachment.getName() ).build();
    }

    public com.enonic.wem.api.content.attachment.Attachments toContentAttachments(
        final com.enonic.wem.api.node.Attachments nodeAttachments )
    {
        final com.enonic.wem.api.content.attachment.Attachments.Builder attachmentsBuilder =
            com.enonic.wem.api.content.attachment.Attachments.builder();
        for ( com.enonic.wem.api.node.Attachment entityAttachment : nodeAttachments )
        {
            if ( !entityAttachment.name().equals( ThumbnailAttachmentSerializer.THUMB_NAME ) )
            {
                attachmentsBuilder.add( toContentAttachment( entityAttachment ) );
            }
        }
        return attachmentsBuilder.build();
    }

    public Attachment toContentAttachment( final com.enonic.wem.api.node.Attachment entityAttachment )
    {
        if ( entityAttachment == null )
        {
            return null;
        }

        return doTranslateToContentAttachment( entityAttachment );
    }

    private Attachment doTranslateToContentAttachment( final com.enonic.wem.api.node.Attachment nodeAttachment )
    {
        return Attachment.newAttachment().
            blobKey( nodeAttachment.blobKey() ).
            mimeType( nodeAttachment.mimeType() ).
            name( nodeAttachment.name() ).
            size( nodeAttachment.size() ).
            build();
    }
}

