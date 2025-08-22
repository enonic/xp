package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

abstract class AbstractContentCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractContentCommand.class );

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

    private static Predicate<ContentTypeName> allowContentTypeFilter( final ApplicationKey applicationKey, final List<String> wildcards )
    {
        final ApplicationWildcardMatcher<ContentTypeName> wildcardMatcher =
            new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString );
        return wildcards.stream().map( wildcardMatcher::createPredicate ).reduce( Predicate::or ).orElse( s -> true );
    }

    Content getContent( final ContentId contentId )
    {
        final Content content = GetContentByIdCommand.create( contentId, this ).build().execute();
        if ( content == null )
        {
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( ContextAccessor.current().getRepositoryId() )
                .branch( ContextAccessor.current().getBranch() )
                .contentRoot( ContentNodeHelper.getContentRoot() )
                .build();
        }
        return content;
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
        return contents.stream().filter( content -> !this.contentPendingOrExpired( content, now ) ).collect( Contents.collector() );
    }

    protected Content filterScheduledPublished( Content content )
    {
        final Instant now = Instant.now();
        return contentPendingOrExpired( content, now ) ? null : content;
    }

    protected boolean shouldFilterScheduledPublished()
    {
        //Returns true if the command is executed on master and the flag ignorePublishTimes has not been set on the context
        final Context currentContext = ContextAccessor.current();
        return !Boolean.TRUE.equals( currentContext.getAttribute( "ignorePublishTimes" ) ) &&
            ContentConstants.BRANCH_MASTER.equals( currentContext.getBranch() );
    }

    protected boolean contentPendingOrExpired( final Content content, final Instant now )
    {
        final ContentPublishInfo publishInfo = content.getPublishInfo();
        if ( publishInfo != null )
        {
            //If publishTo is before the current time or publishFrom after the current time
            return ( publishInfo.getTo() != null && publishInfo.getTo().compareTo( now ) < 0 ) ||
                ( publishInfo.getFrom() != null && publishInfo.getFrom().compareTo( now ) > 0 );
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
            return ( publishTo != null && publishTo.compareTo( now ) < 0 ) || ( publishFrom != null && publishFrom.compareTo( now ) > 0 );
        }
        return false;
    }

    protected Filters createFilters()
    {
        if ( shouldFilterScheduledPublished() )
        {
            final BooleanFilter notPendingFilter = BooleanFilter.create()
                .mustNot( RangeFilter.create()
                              .fieldName( ContentIndexPath.PUBLISH_FROM.getPath() )
                              .from( ValueFactory.newDateTime( Instant.now() ) )
                              .build() )
                .build();
            final BooleanFilter notExpiredFilter = BooleanFilter.create()
                .mustNot( RangeFilter.create()
                              .fieldName( ContentIndexPath.PUBLISH_TO.getPath() )
                              .to( ValueFactory.newDateTime( Instant.now() ) )
                              .build() )
                .build();
            return Filters.from( notPendingFilter, notExpiredFilter );
        }
        return Filters.from();
    }

    protected <T> T runAsAdmin( final Callable<T> callable )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( ContentConstants.CONTENT_SU_AUTH_INFO )
            .build()
            .callWith( callable );
    }

    protected void validateParentChildRelations( final ContentPath parentPath, final ContentTypeName typeName )
    {
        if ( parentPath.isRoot() )
        {
            // root allows anything except page-template and template-folder.
            if ( typeName.isPageTemplate() || typeName.isTemplateFolder() )
            {
                throw new IllegalArgumentException( String.format( "A content with type '%s' cannot be a child of root", typeName ) );
            }
            else
            {
                return;
            }
        }

        final Content parent = GetContentByPathCommand.create( parentPath, this ).build().execute();
        if (parent == null)
        {
            throw new IllegalStateException( String.format( "Cannot read parent type with path %s", parentPath ) );
        }

        final ContentTypeName parentTypeName = parent.getType();

        if ( ( typeName.isTemplateFolder() && !parentTypeName.isSite() ) ||
            ( typeName.isPageTemplate() && !parentTypeName.isTemplateFolder() ) )
        {
            throw new IllegalArgumentException(
                String.format( "A content with type '%s' cannot be a child of '%s' with path %s", typeName, parentTypeName, parentPath ) );
        }

        final ContentType parentType = contentTypeService.getByName( GetContentTypeParams.from( parentTypeName ) );
        if ( parentType == null )
        {
            LOG.debug( "Bypass validation for unknown content type of parent with path {}", parentPath );
            return;
        }

        if ( !parentType.allowChildContent() )
        {
            throw new IllegalArgumentException(
                String.format( "Child content is not allowed in '%s' with path %s", parentTypeName, parentPath ) );
        }

        final boolean isAllowed =
            allowContentTypeFilter( parentTypeName.getApplicationKey(), parentType.getAllowChildContentType() ).test( typeName );

        if ( !isAllowed )
        {
            throw new IllegalArgumentException(
                String.format( "A content with type '%s' cannot be a child of '%s' with path %s", typeName, parentTypeName, parentPath ) );
        }
    }

    public static class Builder<B extends Builder<B>>
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
            Objects.requireNonNull( nodeService );
            Objects.requireNonNull( contentTypeService );
            Objects.requireNonNull( eventPublisher );
            Objects.requireNonNull( translator );
        }
    }

}
