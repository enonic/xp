package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.ResolveSyncWorkResults;
import com.enonic.xp.node.SyncWorkResolverParams;

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
            pushWithDependencies();
        }
        else
        {
            pushWithoutDependencyResolve();
        }

        return resultBuilder.build();
    }

    private void pushWithoutDependencyResolve()
    {
        final GetContentByIdsParams getContentParams = new GetContentByIdsParams( this.contentIds ).setGetChildrenIds( false );
        final boolean validContents = ensureValidContents( getContentByIds( getContentParams ) );

        if ( validContents )
        {
            final NodeIds nodesToPush = ContentNodeHelper.toNodeIds( this.contentIds );
            doPushNodes( nodesToPush );
        }
    }

    private void pushWithDependencies()
    {
        final ResolveSyncWorkResults syncWorkResults = resolveSyncWorkResults();

        appendSyncWorkResultsToResult( syncWorkResults );

        if ( isContinuePush( syncWorkResults ) )
        {
            executePushAndDeletes( syncWorkResults );
        }
    }

    private ResolveSyncWorkResults resolveSyncWorkResults()
    {
        final ResolveSyncWorkResults.Builder resultsBuilder = ResolveSyncWorkResults.create();

        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId );

            resultsBuilder.add( syncWorkResult );
        }
        return resultsBuilder.build();
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId )
    {
        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( true ).
            nodeId( NodeId.from( contentId.toString() ) ).
            branch( this.target ).
            build() );
    }

    private void executePushAndDeletes( final ResolveSyncWorkResults results )
    {
        for ( final ResolveSyncWorkResult result : results )
        {
            final NodeIds nodesToPush = NodeIds.from( result.getNodePublishRequests().getNodeIds() );

            final Contents contents = getContentByIds( new GetContentByIdsParams( ContentNodeHelper.toContentIds( nodesToPush ) ) );

            final boolean validContents = ensureValidContents( contents );

            if ( validContents )
            {
                doPushNodes( nodesToPush );

                doDeleteNodes( result );
            }
        }
    }

    private void doPushNodes( final NodeIds nodesToPush )
    {
        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, this.target );

        appendPushNodesResult( pushNodesResult );

        publishNodePublishedEvents( pushNodesResult );
    }

    private void publishNodePublishedEvents( final PushNodesResult pushNodesResult )
    {
        final List<ContentPath> publishedContentPaths = pushNodesResult.getSuccessfull().stream().
            map( ( node ) -> translateNodePathToContentPath( node.path() ) ).
            collect( toList() );
        if ( !publishedContentPaths.isEmpty() )
        {
            final ContentPaths contentPaths = ContentPaths.from( publishedContentPaths );
            eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.PUBLISH, contentPaths ) );
        }
    }

    private void doDeleteNodes( final ResolveSyncWorkResult result )
    {
        final Context currentContext = ContextAccessor.current();

        final List<ContentPath> deletedContents = new ArrayList<>();
        deletedContents.addAll( deleteNodesInContext( result, currentContext ) );

        deletedContents.addAll( deleteNodesInContext( result, ContextBuilder.from( currentContext ).
            branch( target ).
            build() ) );

        for ( final NodeId nodeId : result.getDelete() )
        {
            this.resultBuilder.addDeleted( ContentId.from( nodeId.toString() ) );
        }

        if ( !deletedContents.isEmpty() )
        {
            eventPublisher.publish(
                ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DELETE, ContentPaths.from( deletedContents ) ) );
        }
    }

    private List<ContentPath> deleteNodesInContext( final ResolveSyncWorkResult result, final Context context )
    {
        return context.callWith( () -> {
            final List<ContentPath> deletedNodes = new ArrayList<>();
            for ( final NodeId nodeId : result.getDelete() )
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

    private void appendSyncWorkResultsToResult( final ResolveSyncWorkResults syncWorkResults )
    {
        this.resultBuilder.pushContentRequests( PushContentRequestsFactory.create( syncWorkResults ) );
    }

    private void appendPushNodesResult( final PushNodesResult pushNodesResult )
    {
        this.resultBuilder.setPushedContent( translator.fromNodes( pushNodesResult.getSuccessfull() ) );

        for ( final PushNodesResult.Failed failedNode : pushNodesResult.getFailed() )
        {
            final Content content = translator.fromNode( failedNode.getNode() );

            final PushContentsResult.FailedReason failedReason;

            switch ( failedNode.getReason() )
            {
                case PARENT_NOT_FOUND:
                {
                    failedReason = PushContentsResult.FailedReason.PARENT_NOT_EXISTS;
                    break;
                }
                case ACCESS_DENIED:
                {
                    failedReason = PushContentsResult.FailedReason.ACCESS_DENIED;
                    break;
                }
                default:
                {
                    failedReason = PushContentsResult.FailedReason.UNKNOWN;
                }

            }

            this.resultBuilder.addFailed( content, failedReason );
        }
    }

    private boolean isContinuePush( final ResolveSyncWorkResults results )
    {
        return !results.hasNotice() || !strategy.equals( PushContentStrategy.STRICT );
    }

    private boolean ensureValidContents( final Contents contents )
    {
        boolean allOk = true;

        for ( final Content content : contents )
        {
            if ( !content.isValid() )
            {
                this.resultBuilder.addFailed( content, PushContentsResult.FailedReason.CONTENT_NOT_VALID );
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
