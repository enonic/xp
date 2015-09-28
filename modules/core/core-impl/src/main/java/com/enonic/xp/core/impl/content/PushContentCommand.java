package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;
import static java.util.stream.Collectors.toList;

public class PushContentCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Branch target;

    private final PushContentStrategy strategy;

    private final boolean resolveDependencies;

    private final PushContentsResult.Builder resultBuilder;

    private final boolean includeChildren;

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.strategy = builder.strategy;
        this.resolveDependencies = builder.resolveDependencies;
        this.includeChildren = builder.includeChildren;
        this.resultBuilder = PushContentsResult.create();
    }

    PushContentsResult execute()
    {
        if ( resolveDependencies )
        {
            pushAndDelete( getWithDependents() );
        }
        else
        {
            pushAndDelete( this.contentIds );
        }

        this.nodeService.refresh();

        return resultBuilder.build();
    }

    private void pushAndDelete( final ContentIds contentIds )
    {
        final Contents contentsToPush = getContentByIds( new GetContentByIdsParams( contentIds ).setGetChildrenIds( false ) );

        final boolean validContents = ensureValidContents( contentsToPush );

        if ( !validContents )
        {
            return;
        }

        NodeIds.Builder pushContentsIds = NodeIds.create();
        NodeIds.Builder deletedContentsIds = NodeIds.create();

        final CompareContentResults contentsComparisons = CompareContentsCommand.create().
            nodeService( this.nodeService ).
            contentIds( contentIds ).
            target( this.target ).
            build().
            execute();

        for ( CompareContentResult compareResult : contentsComparisons )
        {
            if ( compareResult.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                deletedContentsIds.add( NodeId.from( compareResult.getContentId() ) );
            }
            else
            {
                pushContentsIds.add( NodeId.from( compareResult.getContentId() ) );
            }
        }

        doPushNodes( pushContentsIds.build() );
        doDeleteNodes( deletedContentsIds.build() );

    }

    private ContentIds getWithDependents()
    {
        final ResolvePublishDependenciesResult resolvedResult = ResolvePublishDependenciesCommand.create().
            contentIds( this.contentIds ).
            includeChildren( this.includeChildren ).
            target( this.target ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            nodeService( this.nodeService ).
            build().
            execute();

        return resolvedResult.contentIds();
    }


    private void doPushNodes( final NodeIds nodesToPush )
    {
        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, this.target );

        pushNodesResult.getSuccessful();

        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( pushNodesResult.getSuccessful() ) );

        publishNodePublishedEvents( pushNodesResult );
    }

    private void doDeleteNodes( final NodeIds nodesToDelete )
    {
        final Context currentContext = ContextAccessor.current();

        final List<ContentPath> deletedContents = new ArrayList<>();
        deletedContents.addAll( deleteNodesInContext( nodesToDelete, currentContext ) );

        deletedContents.addAll( deleteNodesInContext( nodesToDelete, ContextBuilder.from( currentContext ).
            branch( target ).
            build() ) );

        if ( !deletedContents.isEmpty() )
        {
            eventPublisher.publish(
                ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DELETE, ContentPaths.from( deletedContents ) ) );
        }
    }

    private void publishNodePublishedEvents( final PushNodesResult pushNodesResult )
    {
        final NodeIds successful = pushNodesResult.getSuccessful();

        final Contents publishedContents =
            GetContentByIdsCommand.create( new GetContentByIdsParams( ContentNodeHelper.toContentIds( successful ) ) ).
                translator( this.translator ).
                contentTypeService( this.contentTypeService ).
                eventPublisher( this.eventPublisher ).
                nodeService( this.nodeService ).
                build().
                execute();

        final List<ContentPath> publishedContentPaths = publishedContents.stream().
            map( Content::getPath ).
            collect( toList() );

        if ( !publishedContentPaths.isEmpty() )
        {
            final ContentPaths contentPaths = ContentPaths.from( publishedContentPaths );
            eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.PUBLISH, contentPaths ) );
        }
    }

    private List<ContentPath> deleteNodesInContext( final NodeIds nodeIds, final Context context )
    {
        return context.callWith( () -> {
            final List<ContentPath> deletedNodes = new ArrayList<>();
            for ( final NodeId nodeId : nodeIds )
            {
                final Node node = nodeService.deleteById( nodeId );
                if ( node != null )
                {
                    deletedNodes.add( translateNodePathToContentPath( node.path() ) );
                }
            }
            return deletedNodes;
        } );
    }


    private boolean ensureValidContents( final Contents contents )
    {
        boolean allOk = true;

        for ( final Content content : contents )
        {
            if ( !content.isValid() )
            {
                allOk = false;
            }
        }

        return allOk;
    }

    private Contents getContentByIds( final GetContentByIdsParams getContentParams )
    {
        return GetContentByIdsCommand.create( getContentParams ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Branch target;

        private PushContentStrategy strategy = PushContentStrategy.STRICT;

        private boolean resolveDependencies = true;

        private boolean includeChildren = true;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder strategy( final PushContentStrategy strategy )
        {
            this.strategy = strategy;
            return this;
        }

        public Builder resolveDependencies( final boolean resolveDependencies )
        {
            this.resolveDependencies = resolveDependencies;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public PushContentCommand build()
        {
            validate();
            return new PushContentCommand( this );
        }

    }

    public static enum PushContentStrategy
    {
        IGNORE_CONFLICTS,
        ALLOW_PUBLISH_OUTSIDE_SELECTION,
        STRICT
    }
}