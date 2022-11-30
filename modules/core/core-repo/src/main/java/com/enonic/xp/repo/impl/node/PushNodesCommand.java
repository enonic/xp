package com.enonic.xp.repo.impl.node;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreMovedNodeParams;
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
        this.pushListener = Objects.requireNonNullElse( builder.pushListener, c -> {
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public InternalPushNodesResult execute()
    {
        final Context context = ContextAccessor.current();

        refresh( RefreshMode.ALL );

        final NodeBranchEntries nodeBranchEntries = this.nodeStorageService.getBranchNodeVersions( ids, InternalContext.from( context ) );

        final NodeComparisons comparisons = getNodeComparisons( ids );

        final InternalPushNodesResult.Builder builder =
            InternalPushNodesResult.create().targetBranch( this.target ).targetRepo( context.getRepositoryId() );

        final List<NodeBranchEntry> list =
            nodeBranchEntries.getSet().stream().sorted( Comparator.comparing( NodeBranchEntry::getNodePath ) )
            .collect( Collectors.toList() );

        final Set<NodePath> alreadyAdded = new HashSet<>();
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
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( comparison.getCompareStatus() == CompareStatus.EQUAL )
            {
                builder.addSuccess( nodeBranchEntry );
                alreadyAdded.add( nodeBranchEntry.getNodePath() );
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( ( CompareStatus.NEW == comparison.getCompareStatus() || CompareStatus.MOVED == comparison.getCompareStatus() ) &&
                targetAlreadyExists( nodeBranchEntry.getNodePath(), comparisons, context ) )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.ALREADY_EXIST );
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( !alreadyAdded.contains( nodeBranchEntry.getNodePath().getParentPath() ) &&
                !targetParentExists( nodeBranchEntry.getNodePath(), context ) )
            {
                builder.addFailed( nodeBranchEntry, PushNodesResult.Reason.PARENT_NOT_FOUND );
                pushListener.nodesPushed( 1 );
                continue;
            }

            builder.addSuccess( nodeBranchEntry, comparison.getTargetPath() );
            alreadyAdded.add( nodeBranchEntry.getNodePath() );

            if ( comparison.getCompareStatus() == CompareStatus.MOVED )
            {
                updateTargetChildrenMetaData( nodeBranchEntry, builder, alreadyAdded );
            }
        }

        final InternalPushNodesResult result = builder.build();

        final InternalContext pushContext = InternalContext.create( context ).skipConstraints( true ).build();
        this.nodeStorageService.push( result.getPushNodeEntries(), pushListener, pushContext );

        refresh( RefreshMode.ALL );

        return result;
    }

    private NodeComparisons getNodeComparisons( final NodeIds nodeIds )
    {
        return CompareNodesCommand.create().
            nodeIds( nodeIds ).
            storageService( this.nodeStorageService ).
            target( this.target ).
            build().
            execute();
    }

    private void updateTargetChildrenMetaData( final NodeBranchEntry nodeBranchEntry, final InternalPushNodesResult.Builder resultBuilder,
                                               final Set<NodePath> alreadyAdded )
    {
        final Context context = ContextAccessor.current();

        final Context targetContext = ContextBuilder.create().
            authInfo( context.getAuthInfo() ).
            branch( this.target ).
            repositoryId( context.getRepositoryId() ).
            build();

        final FindNodesByParentResult result = FindNodeIdsByParentCommand.create( this )
            .parentPath( nodeBranchEntry.getNodePath() )
            .build()
            .execute();

        final NodeBranchEntries childEntries =
            this.nodeStorageService.getBranchNodeVersions( result.getNodeIds(), InternalContext.from( ContextAccessor.current() ) );

        for ( final NodeBranchEntry child : childEntries )
        {
            final NodeBranchEntry targetNodeEntry =
                this.nodeStorageService.getBranchNodeVersion( child.getNodeId(), InternalContext.from( targetContext ) );

            if ( targetNodeEntry != null )
            {
                final Node childNode = doGetById( child.getNodeId() );

                this.nodeStorageService.move( StoreMovedNodeParams.create().
                    nodeVersionId( child.getVersionId() ).
                    node( childNode ).
                    build(), InternalContext.from( targetContext ) );

                resultBuilder.addSuccess( child );
                alreadyAdded.add( child.getNodePath() );

                updateTargetChildrenMetaData( child, resultBuilder, alreadyAdded );
            }
        }
    }

    private boolean targetAlreadyExists( final NodePath nodePath, final NodeComparisons comparisons, final Context currentContext )
    {
        //Checks if a node exist
        final Context targetContext = createTargetContext( currentContext );
        final NodeId nodeId = targetContext.callWith( () -> GetNodeIdByPathCommand.create( this ).
            nodePath( nodePath ).
            build().
            execute() );

        //If the node does not exist, returns false
        if ( nodeId == null )
        {
            return false;
        }

        //Else, if the existing node is being moved during the current push, returns false
        final NodeComparison nodeComparison = comparisons.get( nodeId );
        return nodeComparison == null || CompareStatus.MOVED != nodeComparison.getCompareStatus();
    }

    private boolean targetParentExists( final NodePath nodePath, final Context currentContext )
    {
        if ( nodePath.isRoot() || nodePath.getParentPath().equals( NodePath.ROOT ) )
        {
            return true;
        }

        final Context targetContext = createTargetContext( currentContext );

        return targetContext.callWith( CheckNodeExistsCommand.create( this ).nodePath( nodePath.getParentPath() ).build()::execute );
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
