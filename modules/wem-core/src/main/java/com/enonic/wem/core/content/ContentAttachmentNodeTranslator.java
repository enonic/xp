package com.enonic.wem.core.content;

import java.util.Collection;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.Attachments;

public class ContentAttachmentNodeTranslator
{

    public Attachments toNodeAttachments( final Collection<Attachment> contentAttachments )
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
            final com.enonic.wem.api.entity.Attachment attachment = doTranslateToNodeAttachment( contentAttachment );

            attachmentsBuilder.add( attachment );
        }

        return attachmentsBuilder.build();
    }

    private com.enonic.wem.api.entity.Attachment doTranslateToNodeAttachment( final Attachment contentAttachment )
    {
        return com.enonic.wem.api.entity.Attachment.newAttachment().blobKey( contentAttachment.getBlobKey() ).
            mimeType( contentAttachment.getMimeType() ).
            size( contentAttachment.getSize() ).
            name( contentAttachment.getName() ).build();
    }


    public Attachment toContentAttachment( final com.enonic.wem.api.entity.Attachment entityAttachment )
    {
        if ( entityAttachment == null )
        {
            return null;
        }

        return doTranslateToContentAttachment( entityAttachment );
    }

    private Attachment doTranslateToContentAttachment( final com.enonic.wem.api.entity.Attachment nodeAttachment )
    {
        return Attachment.newAttachment().
            blobKey( nodeAttachment.blobKey() ).
            mimeType( nodeAttachment.mimeType() ).
            name( nodeAttachment.name() ).
            size( nodeAttachment.size() ).
            build();
    }
}

