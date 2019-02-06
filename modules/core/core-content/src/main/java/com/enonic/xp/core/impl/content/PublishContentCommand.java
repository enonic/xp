package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.DeleteContentListener;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PublishContentCommand
    extends AbstractContentCommand
    implements PushNodesListener, DeleteNodeListener
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final Branch target;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeDependencies;

    private final boolean resolveSyncWork = true;

    private final PublishContentResult.Builder resultBuilder;

    private final PushContentListener pushContentListener;

    private final DeleteContentListener deleteNodeListener;

    private PublishContentCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.contentPublishInfo = builder.contentPublishInfo;
        this.includeDependencies = builder.includeDependencies;
        this.excludeChildrenIds = builder.excludeChildrenIds;
        this.resultBuilder = PublishContentResult.create();
        this.pushContentListener = builder.pushContentListener;
        this.deleteNodeListener = builder.deleteNodeListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    PublishContentResult execute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        final CompareContentResults results;

        if ( resolveSyncWork )
        {
            results = getSyncWork();
        }
        else
        {
            results = CompareContentsCommand.create().
                contentIds( this.contentIds ).
                nodeService( this.nodeService ).
                target( this.target ).
                build().
                execute();
        }
        if ( pushContentListener != null )
        {
            pushContentListener.contentResolved( results.size() );
        }
        pushAndDelete( results );

        this.nodeService.refresh( RefreshMode.ALL );

        return resultBuilder.build();
    }

    private void pushAndDelete( final CompareContentResults results )
    {
        NodeIds.Builder pushNodesIds = NodeIds.create();
        NodeIds.Builder deletedNodesIds = NodeIds.create();

        for ( CompareContentResult compareResult : results )
        {
            if ( compareResult.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                deletedNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
            else
            {
                pushNodesIds.add( NodeId.from( compareResult.getContentId() ) );
            }
        }

        final ContentIds pushContentsIds = ContentIds.from( pushNodesIds.build().stream().
            map( ( n ) -> ContentId.from( n.toString() ) ).
            toArray( ContentId[]::new ) );

        final boolean validContents = checkIfAllContentsValid( pushContentsIds );

        if ( validContents )
        {
            doPushNodes( pushNodesIds.build() );
        }
        else
        {
            this.resultBuilder.setFailed( pushContentsIds );
        }

        doDeleteNodes( deletedNodesIds.build() );
    }

    private CompareContentResults getSyncWork()
    {
        return ResolveContentsToBePublishedCommand.create().
            contentIds( this.contentIds ).
            excludedContentIds( this.excludedContentIds ).
            excludeChildrenIds( this.excludeChildrenIds ).
            includeDependencies( this.includeDependencies ).
            target( this.target ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    private boolean checkIfAllContentsValid( final ContentIds pushContentsIds )
    {
        final ContentIds invalidContentIds = CheckContentsValidCommand.create().
            translator( this.translator ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            contentTypeService( this.contentTypeService ).
            contentIds( pushContentsIds ).
            build().
            execute();

        return invalidContentIds.isEmpty();
    }

    private void doPushNodes( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return;
        }

        SetPublishInfoCommand.create( this ).
            nodeIds( nodesToPush ).
            contentPublishInfo( contentPublishInfo ).
            pushListener( pushContentListener ).
            build().
            execute();

        final PushNodesResult pushNodesResult = nodeService.push( nodesToPush, this.target, this );

        commitPushedNodes( pushNodesResult.getSuccessful() );

        this.resultBuilder.setFailed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getFailed().
            stream().map( failed -> failed.getNodeBranchEntry().getNodeId() ).collect( Collectors.toList() ) ) ) );
        this.resultBuilder.setPushed( ContentNodeHelper.toContentIds( NodeIds.from( pushNodesResult.getSuccessful().getKeys() ) ) );
    }

    private void commitPushedNodes( final NodeBranchEntries branchEntries )
    {
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().
            message( "Publish" ).
            build();
        final RoutableNodeVersionIds.Builder routableNodeVersionIds = RoutableNodeVersionIds.create();
        for ( NodeBranchEntry branchEntry : branchEntries )
        {
            final RoutableNodeVersionId routableNodeVersionId =
                RoutableNodeVersionId.from( branchEntry.getNodeId(), branchEntry.getVersionId() );
            routableNodeVersionIds.add( routableNodeVersionId );
        }
        nodeService.commit( commitEntry, routableNodeVersionIds.build() );
    }


    private void doDeleteNodes( final NodeIds nodeIdsToDelete )
    {
        final ContentIds contentIdsToDelete = ContentNodeHelper.toContentIds( NodeIds.from( nodeIdsToDelete ) );
        this.resultBuilder.setDeleted( contentIdsToDelete );

        try
        {
            if ( nodeIdsToDelete.getSize() == 1 )
            {
                final Node nodeToDelete = nodeService.getById( nodeIdsToDelete.first() );
                final ContentPath contentPathToDelete = ContentNodeHelper.translateNodePathToContentPath( nodeToDelete.path() );
                this.resultBuilder.setDeletedPath( contentPathToDelete );
            }

            totalToDelete( nodeIdsToDelete.getSize() * 2 );

            final Context currentContext = ContextAccessor.current();
            deleteNodesInContext( nodeIdsToDelete, currentContext, this );
            deleteNodesInContext( nodeIdsToDelete, ContextBuilder.from( currentContext ).
                branch( target ).
                build(), this );
        }
        catch ( NodeNotFoundException e )
        {
            // node to delete doesn't exist
        }

        if ( pushContentListener != null )
        {
            pushContentListener.contentPushed( contentIdsToDelete.getSize() );
        }
    }

    private void deleteNodesInContext( final NodeIds nodeIds, final Context context, final DeleteNodeListener deleteNodeListener )
    {
        context.callWith( () -> {
            nodeIds.forEach( nodeId -> nodeService.deleteById( nodeId, deleteNodeListener ) );
            return null;
        } );
    }

    @Override
    public void nodesPushed( final int count )
    {
        if ( pushContentListener != null )
        {
            pushContentListener.contentPushed( count );
        }
    }

    @Override
    public void nodesDeleted( final int count )
    {
        if ( deleteNodeListener != null )
        {
            deleteNodeListener.contentDeleted( count );
        }
    }

    @Override
    public void totalToDelete( final int count )
    {
        if ( deleteNodeListener != null )
        {
            deleteNodeListener.setTotal( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private Branch target;

        private ContentPublishInfo contentPublishInfo;

        private boolean includeDependencies = true;

        private PushContentListener pushContentListener;

        private DeleteContentListener deleteNodeListener;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( final ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder contentPublishInfo( final ContentPublishInfo contentPublishInfo )
        {
            if ( contentPublishInfo != null && contentPublishInfo.getFrom() == null )
            {
                this.contentPublishInfo = ContentPublishInfo.create().
                    from( Instant.now() ).
                    to( contentPublishInfo.getFrom() ).
                    build();
            }
            else
            {
                this.contentPublishInfo = contentPublishInfo;
            }
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder pushListener( final PushContentListener pushContentListener )
        {
            this.pushContentListener = pushContentListener;
            return this;
        }

        public Builder deleteListener( final DeleteContentListener deleteNodeListener )
        {
            this.deleteNodeListener = deleteNodeListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
            if ( contentPublishInfo != null )
            {
                final Instant publishToInstant = contentPublishInfo.getTo();
                if ( publishToInstant != null )
                {
                    final Instant publishFromInstant = contentPublishInfo.getFrom();
                    Preconditions.checkArgument( publishToInstant.compareTo( publishFromInstant ) >= 0,
                                                 "'Publish to' must be set after 'Publish from'." );
                }
            }
        }

        public PublishContentCommand build()
        {
            validate();
            return new PublishContentCommand( this );
        }

    }
}
