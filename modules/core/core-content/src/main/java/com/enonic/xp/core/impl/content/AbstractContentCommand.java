package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
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

    protected Contents filter( Contents contents )
    {
        return filterScheduledPublished( contents );
    }

    protected Content filter( Content content )
    {
        return filterScheduledPublished( content );
    }

    protected Contents filterScheduledPublished( Contents contents )
    {
        if ( shouldFilterScheduledPublished() )
        {
            final Instant now = Instant.now();
            final List<Content> filteredContentList = contents.stream().
                filter( content -> this.contentPendingOrExpired( content, now ) ).
                collect( Collectors.toList() );
            return Contents.from( filteredContentList );
        }

        //Else, returns the content
        return contents;
    }


    protected Content filterScheduledPublished( Content content )
    {
        if ( shouldFilterScheduledPublished() )
        {
            final Instant now = Instant.now();
            if ( contentPendingOrExpired( content, now ) )
            {
                return null;
            }
        }
        return content;
    }

    protected boolean shouldFilterScheduledPublished()
    {
        //If the command is executed on master and the flag includeScheduledPublished has not been set on the context
        final Context currentContext = ContextAccessor.current();
        return !Boolean.TRUE.equals( currentContext.getAttribute( "includeScheduledPublished" ) ) &&
            ContentConstants.BRANCH_MASTER.equals( currentContext.getBranch() );
    }

    protected boolean contentPendingOrExpired( final Content content, final Instant now )
    {
        final ContentPublishInfo publishInfo = content.getPublishInfo();
        if ( publishInfo != null )
        {
            //If the content is expired or pending publish 
            if ( ( publishInfo.getTo() != null && publishInfo.getTo().compareTo( now ) < 0 ) ||
                ( publishInfo.getFrom() != null && publishInfo.getFrom().compareTo( now ) > 0 ) )
            {
                //Filters the content
                return true;
            }
        }
        return false;
    }

    protected boolean contentPendingOrExpired( final Node node, final Instant now )
    {
        final PropertyTree data = node.data();
        if ( data.hasProperty( ContentPropertyNames.PUBLISH_INFO ) )
        {
            final Instant publishFrom = data.getInstant( ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_FROM );
            final Instant publishTo = data.getInstant( ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_TO );
            if ( ( publishTo != null && publishTo.compareTo( now ) < 0 ) || ( publishFrom != null && publishFrom.compareTo( now ) > 0 ) )
            {
                //Filters the content
                return true;
            }
        }
        return false;
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
