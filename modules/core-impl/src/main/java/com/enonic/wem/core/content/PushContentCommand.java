package com.enonic.wem.core.content;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPublishedEvent;
import com.enonic.wem.api.content.PushContentsResult;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.node.SyncWorkResolverParams;
import com.enonic.wem.api.workspace.Workspace;

public class PushContentCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Workspace target;

    private final PushContentStrategy strategy;

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.strategy = builder.strategy;
    }

    PushContentsResult execute()
    {

        final Set<NodeId> toPublish = Sets.newHashSet();
        final Set<NodeId> toDelete = Sets.newHashSet();
        final Set<NodeId> conflicts = Sets.newHashSet();

        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult result = nodeService.resolveSyncWork( SyncWorkResolverParams.create().
                includeChildren( true ).
                nodeId( NodeId.from( contentId.toString() ) ).
                workspace( ContentConstants.WORKSPACE_PROD ).
                build() );

            if ( result.hasConflicts() )
            {
                // Do conflict stuff
            }

            toPublish.addAll( result.getNodePublishRequests().getNodeIds().getSet() );
            toDelete.addAll( result.getDelete().getSet() );
        }

        final PushNodesResult pushNodesResult = nodeService.push( NodeIds.from( toPublish ), ContentConstants.WORKSPACE_PROD );

        PushContentsResult contentResult = createResult( pushNodesResult );

        for ( final Content content : contentResult.getSuccessfull() )
        {
            eventPublisher.publish( new ContentPublishedEvent( content.getId() ) );
        }

        for ( final NodeId nodeId : toDelete )
        {
            nodeService.deleteById( nodeId );
        }

        return contentResult;
    }

    private PushContentsResult createResult( final PushNodesResult pushNodesResult )
    {
        final PushContentsResult.Builder builder = PushContentsResult.create();

        builder.successfull( translator.fromNodes( pushNodesResult.getSuccessfull() ) );

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

            builder.addFailed( content, failedReason );

        }

        return builder.build();
    }

    private NodeIds getAsNodeIds( final ContentIds contentIds )
    {
        final Set<NodeId> nodeIds = Sets.newHashSet();

        for ( final ContentId contentId : contentIds )
        {
            nodeIds.add( NodeId.from( contentId.toString() ) );
        }

        return NodeIds.from( nodeIds );
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

        private final PushContentStrategy strategy = PushContentStrategy.DEFAULT;

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
        STRICT,
        DEFAULT;

    }

}
