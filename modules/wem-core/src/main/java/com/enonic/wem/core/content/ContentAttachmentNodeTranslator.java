package com.enonic.wem.core.content;

import java.util.Collection;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.Attachments;

public class ContentAttachmentNodeTranslator
{

    public Attachments toNodeAttachments( final Collection<Attachment> contentAttachments )
    {
        if ( contentAttachments == null || contentAttachments.isEmpty() )
        {
            return null;
        }

        final Attachments.Builder attachmentsBuilder = Attachments.builder();

        for ( final Attachment contentAttachment : contentAttachments )
        {
            final com.enonic.wem.api.entity.Attachment attachment =
                com.enonic.wem.api.entity.Attachment.newAttachment().blobKey( contentAttachment.getBlobKey() ).
                    mimeType( contentAttachment.getMimeType() ).
                    size( contentAttachment.getSize() ).
                    name( contentAttachment.getName() ).build();

            attachmentsBuilder.add( attachment );
        }

        return attachmentsBuilder.build();
    }
}

