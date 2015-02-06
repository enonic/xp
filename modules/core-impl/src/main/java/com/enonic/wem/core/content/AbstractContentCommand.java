package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;

abstract class AbstractContentCommand
{
    final ContentNodeTranslator translator;

    final NodeService nodeService;

    final ContentTypeService contentTypeService;

    final EventPublisher eventPublisher;

    AbstractContentCommand( final Builder builder )
    {
        this.contentTypeService = builder.contentTypeService;
        this.nodeService = builder.nodeService;
        this.translator = builder.translator;
        this.eventPublisher = builder.eventPublisher;
    }

    Content getContent( final ContentId contentId )
    {
        return GetContentByIdCommand.create( contentId, this ).
            build().
            execute();
    }

    Content getContent( final ContentPath contentPath )
    {
        return GetContentByPathCommand.create( contentPath, this ).
            build().
            execute();
    }

    public static class Builder<B extends Builder>
    {
        private NodeService nodeService;

        private ContentTypeService contentTypeService;

        private ContentNodeTranslator translator;

        private EventPublisher eventPublisher;

        Builder()
        {

        }

        Builder( final AbstractContentCommand source )
        {
            this.translator = source.translator;
            this.nodeService = source.nodeService;
            this.contentTypeService = source.contentTypeService;
            this.eventPublisher = source.eventPublisher;
        }

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B translator( final ContentNodeTranslator translator )
        {
            this.translator = translator;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B eventPublisher( final EventPublisher eventPublisher )
        {
            this.eventPublisher = eventPublisher;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( nodeService, "nodeService cannot be null" );
            Preconditions.checkNotNull( contentTypeService, "contentTypesService cannot be null" );
            Preconditions.checkNotNull( translator, "translator cannot be null" );
            Preconditions.checkNotNull( eventPublisher, "eventPublisher cannot be null" );
        }
    }

}
