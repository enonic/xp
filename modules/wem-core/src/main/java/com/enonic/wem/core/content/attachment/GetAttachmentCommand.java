package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.entity.NoNodeWithIdFoundException;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeService;


final class GetAttachmentCommand
{
    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private final NodeService nodeService;

    private final String attachmentName;

    private final ContentId contentId;

    private GetAttachmentCommand( Builder builder )
    {
        nodeService = builder.nodeService;
        attachmentName = builder.attachmentName;
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    Attachment execute()
    {
        try
        {
            final NodeId nodeId = NodeId.from( this.contentId );
            final Node node = nodeService.getById( nodeId );

            final com.enonic.wem.core.entity.Attachment entityAttachment = node.attachments().getAttachment( this.attachmentName );
            if ( entityAttachment != null )
            {
                return CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment );
            }
            else
            {
                return null;
            }
        }
        catch ( NoNodeWithIdFoundException e )
        {
            throw new ContentNotFoundException( this.contentId, ContextAccessor.current().getWorkspace() );
        }
    }


    public static final class Builder
    {
        private NodeService nodeService;

        private String attachmentName;

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder attachmentName( String attachmentName )
        {
            this.attachmentName = attachmentName;
            return this;
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }


        public GetAttachmentCommand build()
        {
            return new GetAttachmentCommand( this );
        }
    }
}
