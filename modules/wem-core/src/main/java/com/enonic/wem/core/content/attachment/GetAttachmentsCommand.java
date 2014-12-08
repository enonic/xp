package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.NoNodeWithIdFoundException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;


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
            return CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachments( node.attachments() );
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
