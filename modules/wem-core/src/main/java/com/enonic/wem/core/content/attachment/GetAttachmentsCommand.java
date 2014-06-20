package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;


final class GetAttachmentsCommand
{
    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private final NodeService nodeService;

    private final ContentId contentId;

    private final Context context;

    private GetAttachmentsCommand( Builder builder )
    {
        nodeService = builder.nodeService;
        contentId = builder.contentId;
        context = builder.context;
    }

    public static Builder create()
    {
        return new Builder();
    }

    Attachments execute()
    {
        try
        {
            final EntityId entityId = EntityId.from( this.contentId );
            final Node node = nodeService.getById( entityId, this.context );
            final Attachments.Builder attachmentsBuilder = Attachments.builder();

            for ( com.enonic.wem.api.entity.Attachment entityAttachment : node.attachments() )
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
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( this.contentId, this.context.getWorkspace() );
        }
    }


    public static final class Builder
    {
        private NodeService nodeService;

        private ContentId contentId;

        private Context context;

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

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public GetAttachmentsCommand build()
        {
            return new GetAttachmentsCommand( this );
        }
    }
}
