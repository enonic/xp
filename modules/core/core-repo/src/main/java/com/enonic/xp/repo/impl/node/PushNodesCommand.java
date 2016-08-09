package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.MoveNodeParams;
import com.enonic.xp.security.acl.Permission;

public class PushNodesCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodeIds ids;

    private final PushNodesListener pushListener;

    private PushNodesCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.ids = builder.ids;
        this.pushListener = builder.pushListener == null ? this::doNothingPushListener : builder.pushListener;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public PushNodesResult execute()
    {
        final Context context = ContextAccessor.current();

        final NodeBranchEntries nodeBranchEntries = getNodeBranchEntries();
        final NodeComparisons comparisons = getNodeComparisons( nodeBranchEntries );

        final PushNodesResult.Builder builder = pushNodes( context, nodeBranchEntries, comparisons );

        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        return builder.build();
    }

    private PushNodesResult.Builder pushNodes( final Context context, final NodeBranchEntries nodeBranchEntries,
                                               final NodeComparisons comparisons )
    {
        final PushNodeEntries.Builder publishBuilder = PushNodeEntries.create().
            targetBranch( this.target ).
            targetRepo( context.getRepositoryId() );

        final PushNodesResult.Builder builder = PushNodesResult.create();

        final Stopwatch sortTimer = Stopwatch.createStarted();
        final ArrayList<NodeBranchEntry> list = new ArrayList<>( nodeBranchEntries.getSet() );
        Collections.sort( list, ( e1, e2 ) -> e1.getNodePath().compareTo( e2.getNodePath() ) );
        System.out.println( "Ordering collection of NodeBranchEntries: " + sortTimer.stop() );

        for ( final NodeBranchEntry branchEntry : list )
        {
            final NodeComparison comparison = comparisons.get( branchEntry.getNodeId() );

            final NodeBranchEntry nodeBranchEntry = nodeBranchEntries.get( comparison.getNodeId() );

            final boolean hasPublishPermission = NodesHasPermissionResolver.create( this ).
                nodeIds( NodeIds.from( nodeBranchEntry.getNodeId() ) ).
                permission( Permission.PUBLISH ).
                build().
                execute();

            if ( !hasPublishPermission )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.ACCESS_DENIED );
                pushListener.nodePushed( branchEntry.getNodeId(), PushNodesListener.PushResult.FAILED );
                continue;
            }

            if ( comparison.getCompareStatus() == CompareStatus.EQUAL )
            {
                builder.addSuccess( nodeBranchEntry );
                pushListener.nodePushed( branchEntry.getNodeId(), PushNodesListener.PushResult.PUSHED );
                continue;
            }

            if ( !targetParentExists( nodeBranchEntry.getNodePath(), builder, context ) )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.PARENT_NOT_FOUND );
                pushListener.nodePushed( branchEntry.getNodeId(), PushNodesListener.PushResult.FAILED );
                continue;
            }

            publishBuilder.add( PushNodeEntry.create().
                nodeBranchEntry( nodeBranchEntry ).
                nodeVersionId( nodeBranchEntry.getVersionId() ).
                build() );

            //doPushNode( context, nodeBranchEntry, nodeBranchEntry.getVersionId() );
            builder.addSuccess( nodeBranchEntry );
            pushListener.nodePushed( branchEntry.getNodeId(), PushNodesListener.PushResult.PUSHED );

            if ( comparison.getCompareStatus() == CompareStatus.MOVED )
            {
                updateTargetChildrenMetaData( nodeBranchEntry, builder );
            }
        }

        final Stopwatch timer = Stopwatch.createStarted();
        this.storageService.publish( publishBuilder.build(), InternalContext.from( context ) );
        System.out.println( "publish-time: " + timer.stop() );

        return builder;
    }

    private NodeComparisons getNodeComparisons( final NodeBranchEntries nodeBranchEntries )
    {
        return CompareNodesCommand.create().
            nodeIds( NodeIds.from( nodeBranchEntries.getKeys() ) ).
            storageService( this.storageService ).
            target( this.target ).
            build().
            execute();
    }

    private NodeBranchEntries getNodeBranchEntries()
    {
        return FindNodeBranchEntriesByIdCommand.create( this ).
            ids( ids ).
            build().
            execute();
    }

    private void updateTargetChildrenMetaData( final NodeBranchEntry nodeBranchEntry, PushNodesResult.Builder resultBuilder )
    {
        final Context context = ContextAccessor.current();

        final Context targetContext = ContextBuilder.create().
            authInfo( context.getAuthInfo() ).
            branch( this.target ).
            repositoryId( context.getRepositoryId() ).
            build();

        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentPath( nodeBranchEntry.getNodePath() ).
                childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
                build() ).
            build().
            execute();

        final NodeBranchEntries childEntries =
            this.storageService.getBranchNodeVersions( result.getNodeIds(), false, InternalContext.from( ContextAccessor.current() ) );

        for ( final NodeBranchEntry child : childEntries )
        {
            final NodeBranchEntry targetNodeEntry =
                this.storageService.getBranchNodeVersion( child.getNodeId(), InternalContext.from( targetContext ) );

            if ( targetNodeEntry != null )
            {
                final Node childNode = GetNodeByIdCommand.create( this ).
                    id( child.getNodeId() ).
                    build().
                    execute();

                this.storageService.move( MoveNodeParams.create().
                    updateMetadataOnly( true ).
                    node( childNode ).
                    build(), InternalContext.from( targetContext ) );

                resultBuilder.addSuccess( child );

                updateTargetChildrenMetaData( child, resultBuilder );
            }
        }
    }

    private void doPushNode( final Context context, final NodeBranchEntry nodeBranchEntry, final NodeVersionId nodeVersionId )
    {
        this.storageService.publish( nodeBranchEntry, nodeVersionId, InternalContext.create( context ).
            branch( this.target ).
            build(), context.getBranch() );
    }

    private boolean targetParentExists( final NodePath nodePath, final PushNodesResult.Builder builder, final Context currentContext )
    {
        if ( nodePath.isRoot() || nodePath.getParentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        if ( builder.hasBeenAdded( nodePath.getParentPath() ) )
        {
            return true;
        }

        final Context targetContext = createTargetContext( currentContext );

        return targetContext.callWith( () -> CheckNodeExistsCommand.create( this ).
            nodePath( nodePath.getParentPath() ).
            build().
            execute() );
    }

    private Context createTargetContext( final Context currentContext )
    {
        final ContextBuilder targetContext = ContextBuilder.create().
            repositoryId( currentContext.getRepositoryId() ).
            branch( target );

        if ( currentContext.getAuthInfo() != null )
        {
            targetContext.authInfo( currentContext.getAuthInfo() );
        }

        return targetContext.build();
    }

    private void doNothingPushListener( NodeId nodeId, PushNodesListener.PushResult pushResult )
    {
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branch target;

        private NodeIds ids;

        private PushNodesListener pushListener;

        Builder()
        {
            super();
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder ids( final NodeIds nodeIds )
        {
            this.ids = nodeIds;
            return this;
        }

        public Builder pushListener( final PushNodesListener pushListener )
        {
            this.pushListener = pushListener;
            return this;
        }

        public PushNodesCommand build()
        {
            validate();
            return new PushNodesCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( ids );
        }
    }
}
