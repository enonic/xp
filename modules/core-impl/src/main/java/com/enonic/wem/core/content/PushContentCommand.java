package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPublishedEvent;
import com.enonic.wem.api.content.PushContentsResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.node.ResolveSyncWorkResults;
import com.enonic.wem.api.node.SyncWorkResolverParams;
import com.enonic.wem.api.workspace.Workspace;

public class PushContentCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Workspace target;

    private final PushContentStrategy strategy;

    private final boolean resolveDependencies;

    private PushContentsResult.Builder resultBuilder;

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.strategy = builder.strategy;
        this.resolveDependencies = builder.resolveDependencies;
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
        final NodeIds nodesToPush = ContentNodeHelper.toNodeIds( this.contentIds );
        doPushNodes( nodesToPush );
    }

    private void pushWithDependencies()
    {
        final ResolveSyncWorkResults.Builder resultsBuilder = ResolveSyncWorkResults.create();

        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId );

            resultsBuilder.add( syncWorkResult );
        }

        final ResolveSyncWorkResults syncWorkResults = resultsBuilder.build();

        appendSyncWorkResultsToResult( syncWorkResults );

        if ( isContinuePush( syncWorkResults ) )
        {
            executePushAndDeletes( syncWorkResults );
        }
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId )
    {
        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( true ).
            nodeId( NodeId.from( contentId.toString() ) ).
            workspace( this.target ).
            build() );
    }

    private boolean isContinuePush( final ResolveSyncWorkResults results )
    {
        return !results.hasNotice() || !strategy.equals( PushContentStrategy.STRICT );
    }

    private void executePushAndDeletes( final ResolveSyncWorkResults results )
    {
        for ( final ResolveSyncWorkResult result : results )
        {
            final NodeIds nodesToPush = NodeIds.from( result.getNodePublishRequests().getNodeIds() );
            doPushNodes( nodesToPush );

            for ( final NodeId nodeId : result.getDelete() )
            {
                nodeService.deleteById( nodeId );
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
        for ( final Node node : pushNodesResult.getSuccessfull() )
        {
            eventPublisher.publish( new ContentPublishedEvent( ContentId.from( node.id().toString() ) ) );
        }
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
                default:
                {
                    failedReason = PushContentsResult.FailedReason.UNKNOWN;
                }

            }

            this.resultBuilder.addFailed( content, failedReason );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Workspace target;

        private PushContentStrategy strategy = PushContentStrategy.STRICT;

        private boolean resolveDependencies = true;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( final Workspace target )
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
