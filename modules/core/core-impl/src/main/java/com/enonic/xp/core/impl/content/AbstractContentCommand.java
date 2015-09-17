package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.content.ContentTypeService;

abstract class AbstractContentCommand
{
    final NodeService nodeService;

    final ContentTypeService contentTypeService;

    final EventPublisher eventPublisher;

    final ContentNodeTranslator translator;

    AbstractContentCommand( final Builder builder )
    {
        this.contentTypeService = builder.contentTypeService;
        this.nodeService = builder.nodeService;
        this.eventPublisher = builder.eventPublisher;
        this.translator = builder.translator;
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

        private EventPublisher eventPublisher;

        private ContentNodeTranslator translator;

        Builder()
        {
        }

        Builder( final AbstractContentCommand source )
        {
            this.nodeService = source.nodeService;
            this.contentTypeService = source.contentTypeService;
            this.eventPublisher = source.eventPublisher;
            this.translator = source.translator;
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
            Preconditions.checkNotNull( eventPublisher, "eventPublisher cannot be null" );
            Preconditions.checkNotNull( translator, "translator cannot be null" );
        }
    }

}
