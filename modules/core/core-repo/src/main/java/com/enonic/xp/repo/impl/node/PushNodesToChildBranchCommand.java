package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
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
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.MoveNodeParams;

public class PushNodesToChildBranchCommand
    extends AbstractChildBranchCommand
{
    private final NodeIds nodeIds;

    private PushNodesToChildBranchCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
    }

    @Override
    protected void execute( final Branch parentBranch, final Branches childBranches )
    {
        execute( parentBranch, childBranches, nodeIds );
    }

    protected void execute( final Branch parentBranch, final Branches childBranches, final NodeIds nodeIds )
    {
        if ( childBranches.isEmpty() || nodeIds.isEmpty() )
        {
            return;
        }
        refreshAll();
        runInBranch( parentBranch, () -> {
            final NodeBranchEntries nodeBranchEntries = getNodeBranchEntries( nodeIds );
            for ( Branch childBranch : childBranches )
            {
                final NodeComparisons comparisons = getNodeComparisons( nodeBranchEntries, childBranch );
                final NodeIds pushedNodeIds = pushNodes( nodeBranchEntries, comparisons, childBranch ).
                    build().
                    getPushNodeEntries().
                    getNodeIds();
                execute( childBranch, getChildBranches( childBranch ), pushedNodeIds );
            }
        } );
        refreshAll();
    }

    private InternalPushNodesResult.Builder pushNodes( final NodeBranchEntries nodeBranchEntries, final NodeComparisons comparisons,
                                                       final Branch targetBranch )
    {
        final Context targetContext = ContextBuilder.from( ContextAccessor.current() ).branch( targetBranch ).build();
        final PushNodeEntries.Builder publishBuilder = PushNodeEntries.create().
            targetBranch( targetBranch ).
            targetRepo( targetContext.getRepositoryId() );

        final InternalPushNodesResult.Builder builder = InternalPushNodesResult.create();

        final ArrayList<NodeBranchEntry> list = new ArrayList<>( nodeBranchEntries.getSet() );
        list.sort( Comparator.comparing( NodeBranchEntry::getNodePath ) );

        for ( final NodeBranchEntry branchEntry : list )
        {
            final NodeComparison comparison = comparisons.get( branchEntry.getNodeId() );
            final NodeBranchEntry nodeBranchEntry = nodeBranchEntries.get( comparison.getNodeId() );

            if ( comparison.getCompareStatus() != CompareStatus.NEW && !comparison.isTargetInherited() )
            {
                continue;
            }

            if ( comparison.getCompareStatus() == CompareStatus.EQUAL )
            {
                builder.addSuccess( nodeBranchEntry );
                continue;
            }

            if ( ( CompareStatus.NEW == comparison.getCompareStatus() || CompareStatus.MOVED == comparison.getCompareStatus() ) &&
                targetAlreadyExists( nodeBranchEntry.getNodePath(), comparisons, targetContext ) )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.ALREADY_EXIST );
                continue;
            }

            if ( !targetParentExists( nodeBranchEntry.getNodePath(), builder, targetContext ) )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.PARENT_NOT_FOUND );
                continue;
            }

            publishBuilder.add( PushNodeEntry.create().
                nodeBranchEntry( NodeBranchEntry.create( nodeBranchEntry ).inherited( true ).build() ).
                currentTargetPath( comparison.getTargetPath() ).
                build() );

            builder.addSuccess( nodeBranchEntry );

            if ( comparison.getCompareStatus() == CompareStatus.MOVED )
            {
                updateTargetChildrenMetaData( nodeBranchEntry, builder, targetBranch );
            }
        }

        final PushNodeEntries pushNodeEntries = publishBuilder.build();
        builder.setPushNodeEntries( pushNodeEntries );

        final InternalContext pushContext = InternalContext.create( ContextAccessor.current() ).skipConstraints( true ).build();
        this.nodeStorageService.push( pushNodeEntries, null, pushContext );

        return builder;
    }

    private void updateTargetChildrenMetaData( final NodeBranchEntry nodeBranchEntry, final PushNodesResult.Builder resultBuilder,
                                               final Branch targetBranch )
    {
        final Context context = ContextAccessor.current();

        final Context targetContext = ContextBuilder.from( context ).
            branch( targetBranch ).
            build();

        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentPath( nodeBranchEntry.getNodePath() ).
                childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
                build() ).
            build().
            execute();

        final NodeBranchEntries childEntries =
            this.nodeStorageService.getBranchNodeVersions( result.getNodeIds(), false, InternalContext.from( ContextAccessor.current() ) );

        for ( final NodeBranchEntry child : childEntries )
        {
            final NodeBranchEntry targetNodeEntry =
                this.nodeStorageService.getBranchNodeVersion( child.getNodeId(), InternalContext.from( targetContext ) );

            if ( targetNodeEntry != null )
            {
                final Node childNode = GetNodeByIdCommand.create( this ).
                    id( child.getNodeId() ).
                    build().
                    execute();

                this.nodeStorageService.move( MoveNodeParams.create().
                    updateMetadataOnly( true ).
                    node( childNode ).
                    build(), InternalContext.from( targetContext ) );

                resultBuilder.addSuccess( child );

                updateTargetChildrenMetaData( child, resultBuilder, targetBranch );
            }
        }
    }

    private boolean targetAlreadyExists( final NodePath nodePath, final NodeComparisons comparisons, final Context targetContext )
    {
        //Checks if a node exist
        final NodeId nodeId = targetContext.callWith( () -> GetNodeIdByPathCommand.create( this ).
            nodePath( nodePath ).
            build().
            execute() );

        //If the node does not exist, returns false
        if ( nodeId == null )
        {
            return false;
        }

        //Else, if the existing node is being deleted or moved during the current push, returns false
        final NodeComparison nodeComparison = comparisons.get( nodeId );
        if ( nodeComparison != null && ( CompareStatus.MOVED == nodeComparison.getCompareStatus() ||
            CompareStatus.PENDING_DELETE == nodeComparison.getCompareStatus() ) )
        {
            return false;
        }

        return true;
    }

    private boolean targetParentExists( final NodePath nodePath, final PushNodesResult.Builder builder, final Context targetContext )
    {
        if ( nodePath.isRoot() || nodePath.getParentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        if ( builder.hasBeenAdded( nodePath.getParentPath() ) )
        {
            return true;
        }

        return targetContext.callWith( () -> CheckNodeExistsCommand.create( this ).
            nodePath( nodePath.getParentPath() ).
            build().
            execute() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractChildBranchCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        Builder()
        {
            super();
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeIds );
        }

        public PushNodesToChildBranchCommand build()
        {
            validate();
            return new PushNodesToChildBranchCommand( this );
        }
    }
}
