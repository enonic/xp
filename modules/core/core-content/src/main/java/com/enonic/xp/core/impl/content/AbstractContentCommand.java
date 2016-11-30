package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.RangeFilter;
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
        if ( shouldFilterScheduledPublished() )
        {
            return filterScheduledPublished( contents );
        }
        return contents;
    }

    protected Content filter( Content content )
    {
        if ( shouldFilterScheduledPublished() )
        {
            return filterScheduledPublished( content );
        }
        return content;
    }

    protected Contents filterScheduledPublished( Contents contents )
    {
        final Instant now = Instant.now();
        final List<Content> filteredContentList = contents.stream().
            filter( content -> !this.contentPendingOrExpired( content, now ) ).
            collect( Collectors.toList() );
        return Contents.from( filteredContentList );
    }


    protected Content filterScheduledPublished( Content content )
    {
        final Instant now = Instant.now();
        return contentPendingOrExpired( content, now ) ? null : content;
    }

    protected boolean shouldFilterScheduledPublished()
    {
        //Returns true if the command is executed on master and the flag includeScheduledPublished has not been set on the context
        final Context currentContext = ContextAccessor.current();
        return !Boolean.TRUE.equals( currentContext.getAttribute( "includeScheduledPublished" ) ) &&
            ContentConstants.BRANCH_MASTER.equals( currentContext.getBranch() );
    }

    protected boolean contentPendingOrExpired( final Content content, final Instant now )
    {
        final ContentPublishInfo publishInfo = content.getPublishInfo();
        if ( publishInfo != null )
        {
            //If publishTo is before the current time or publishFrom after the current time
            if ( ( publishInfo.getTo() != null && publishInfo.getTo().compareTo( now ) < 0 ) ||
                ( publishInfo.getFrom() != null && publishInfo.getFrom().compareTo( now ) > 0 ) )
            {
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
            //If publishTo is before the current time or publishFrom after the current time
            final Instant publishFrom =
                data.getInstant( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FROM ) );
            final Instant publishTo =
                data.getInstant( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_TO ) );
            if ( ( publishTo != null && publishTo.compareTo( now ) < 0 ) || ( publishFrom != null && publishFrom.compareTo( now ) > 0 ) )
            {
                return true;
            }
        }
        return false;
    }

    protected Filters createFilters()
    {
        if ( shouldFilterScheduledPublished() )
        {
            final BooleanFilter notPendingFilter = BooleanFilter.create().
                mustNot( RangeFilter.create().
                    fieldName( ContentIndexPath.PUBLISH_FROM.getPath() ).
                    from( ValueFactory.newDateTime( Instant.now() ) ).
                    build() ).
                build();
            final BooleanFilter notExpiredFilter = BooleanFilter.create().
                mustNot( RangeFilter.create().
                    fieldName( ContentIndexPath.PUBLISH_TO.getPath() ).
                    to( ValueFactory.newDateTime( Instant.now() ) ).
                    build() ).
                build();
            return Filters.from( notPendingFilter, notExpiredFilter );
        }
        return Filters.from();
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
