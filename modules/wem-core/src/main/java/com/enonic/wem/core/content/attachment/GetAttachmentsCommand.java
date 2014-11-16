package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;
import com.enonic.wem.repo.NoNodeWithIdFoundException;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeService;


final class GetAttachmentsCommand
{
    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private final NodeService nodeService;

    private final ContentId contentId;

    private GetAttachmentsCommand( Builder builder )
    {
        nodeService = builder.nodeService;
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    Attachments execute()
    {
        try
        {
            final NodeId nodeId = NodeId.from( this.contentId );
            final Node node = nodeService.getById( nodeId );
            final Attachments.Builder attachmentsBuilder = Attachments.builder();

            for ( com.enonic.wem.repo.Attachment entityAttachment : node.attachments() )
            {
                final boolean isThumbnail = entityAttachment.name().equals( ThumbnailAttachmentSerializer.THUMB_NAME );

                if ( !isThumbnail )
                {
                    final Attachment attachment = CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment );

                    attachmentsBuilder.add( attachment );
                }
            }

            return attachmentsBuilder.build();
        }
        catch ( NoNodeWithIdFoundException e )
        {
            throw new ContentNotFoundException( this.contentId, ContextAccessor.current().getWorkspace() );
        }
    }


    public static final class Builder
    {
        private NodeService nodeService;

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public GetAttachmentsCommand build()
        {
            return new GetAttachmentsCommand( this );
        }
    }
}
