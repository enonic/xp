package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.ResolveSyncWorkResult;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final ResolveSyncWorkResult.Builder resultBuilder;

    private final Set<NodeId> processedIds;

    private boolean allPossibleNodesAreIncluded;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.repositoryRoot = builder.repositoryRoot;
        this.resultBuilder = ResolveSyncWorkResult.create();
        this.processedIds = Sets.newHashSet();
        this.allPossibleNodesAreIncluded = false;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ResolveSyncWorkResult execute()
    {
        final NodeVersionDiffResult diff = getInitialDiff();

        for ( final NodeId nodeId : diff.getNodesWithDifferences() )
        {
            resolveDiffWithNodeId( nodeId );
        }

        return resultBuilder.build();
    }

    private NodeVersionDiffResult getInitialDiff()
    {
        if ( !includeChildren )
        {
            return NodeVersionDiffResult.create().
                add( this.nodeId ).
                build();
        }

        final NodePath nodePath = resolveDiffRoot();

        return FindNodesWithVersionDifferenceCommand.create().
            versionService( this.versionService ).
            query( NodeVersionDiffQuery.create().
                target( target ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( nodePath ).
                build() ).
            build().
            execute();
    }

    private NodePath resolveDiffRoot()
    {
        final NodePath nodePath;

        if ( this.nodeId != null )
        {
            nodePath = resolveDiffRootFromNodeId();
        }
        else
        {
            nodePath = NodePath.ROOT;
            this.allPossibleNodesAreIncluded = true;
        }
        return nodePath;
    }

    private NodePath resolveDiffRootFromNodeId()
    {
        final NodePath nodePath;
        final Node node = doGetById( nodeId, false );

        if ( node == null )
        {
            throw new IllegalArgumentException( "Node with id: " + this.nodeId + " not found" );
        }

        nodePath = node.path();

        if ( nodePath.equals( repositoryRoot ) )
        {
            this.allPossibleNodesAreIncluded = true;
        }
        return nodePath;
    }

    private void resolveDiffWithNode( final Node node )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node, node.id() );
    }

    private void resolveDiffWithNodeId( final NodeId nodeId )
    {
        if ( isProcessed( nodeId ) )
        {
            return;
        }

        this.processedIds.add( nodeId );

        final Node node = doGetById( nodeId, false );

        if ( node == null )
        {
            // does not exist in source workspace, skip
        }
        else
        {
            doResolveDiff( node, nodeId );
        }
    }

    private void doResolveDiff( final Node node, final NodeId nodeId )
    {
        final NodeComparison comparison = getNodeComparison( nodeId );

        if ( nodeNotChanged( comparison ) )
        {
            return;
        }

        addResult( comparison, node );

        if ( !allPossibleNodesAreIncluded )
        {
            ensureThatParentExists( node );
            includeReferences( node );
        }
    }

    private boolean nodeNotChanged( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.EQUAL;
    }

    private void ensureThatParentExists( final Node node )
    {
        if ( !node.parentPath().equals( NodePath.ROOT ) )
        {
            final Node thisParentNode = doGetByPath( node.parentPath(), false );

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiffWithNode( thisParentNode );
            }
        }
    }

    private void includeReferences( final Node node )
    {
        final Set<Property> references = node.data().getByValueType( ValueTypes.REFERENCE );

        for ( final Property reference : references )
        {
            if ( reference.hasNotNullValue() )
            {
                final NodeId referredNodeId = reference.getReference().getNodeId();

                if ( !this.processedIds.contains( referredNodeId ) )
                {
                    resolveDiffWithNodeId( referredNodeId );
                }
            }
        }
    }

    private boolean shouldBeResolvedDiffFor( final NodeComparison nodeComparison )
    {
        final CompareStatus status = nodeComparison.getCompareStatus();
        return status == CompareStatus.NEW || status == CompareStatus.MOVED;
    }

    private boolean isProcessed( final NodeId nodeId )
    {
        return this.processedIds.contains( nodeId );
    }

    private NodeComparison getNodeComparison( final NodeId nodeId )
    {
        return CompareNodeCommand.create().
            target( this.target ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeId( nodeId ).
            build().
            execute();
    }

    private void addResult( final NodeComparison comparison, final Node nodeToAddToResult )
    {
        final NodeId nodeId = comparison.getNodeId();

        if ( comparison.getCompareStatus().isConflict() )
        {
            resultBuilder.conflict( nodeId );
        }
        else
        {
            ResolveContext resolveContext = determineResolveContext( nodeToAddToResult );
            if ( comparison.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                if ( resolveContext.becauseReferredTo )
                {
                    resultBuilder.deleteReferredFrom( comparison.getNodeId(), resolveContext.contextNodeId );
                }
                else if ( resolveContext.becauseParent )
                {
                    resultBuilder.deleteParentFor( comparison.getNodeId(), resolveContext.contextNodeId );
                }
                else if ( resolveContext.becauseChild )
                {
                    resultBuilder.deleteChildOf( comparison.getNodeId(), resolveContext.contextNodeId );
                }
                else
                {
                    resultBuilder.deleteRequested( comparison.getNodeId() );
                }
            }
            else if ( resolveContext.becauseReferredTo )
            {
                resultBuilder.publishReferredFrom( comparison.getNodeId(), resolveContext.contextNodeId );
            }
            else if ( resolveContext.becauseParent )
            {
                resultBuilder.publishParentFor( comparison.getNodeId(), resolveContext.contextNodeId );
            }
            else if ( resolveContext.becauseChild )
            {
                resultBuilder.publishChildOf( comparison.getNodeId(), resolveContext.contextNodeId );
            }
            else
            {
                resultBuilder.publishRequested( comparison.getNodeId() );
            }
        }
    }

    /**
     * Determines resolveContext with regard to the node that is passed for publishing (represented with this.nodeId).
     * Determination is made in the following way:
     * If passed node's id is equal to this.nodeId, then this node is requested to publish.
     * If passed node's path contains path of initial node (this.nodeId), then it is a child of this.nodeId
     * If passed node's path is contained within path of initial node (this.nodeId), then it is a parent of this.nodeId
     * If this.nodeId is null, all resolved nodes are treated as requested to publish.
     * Otherwise, passed node was referred.
     *
     * @return
     */
     /*
    - S1 (New)
     - A1 (New)
     - A2 (New)
         - A2_1 - Ref:B2_1 (New)
    - S2 (New)
     - B1 (New)
     - B2 (New)
         - B2_1 (New)

    Publish S1 with children, will result in following contexts:
    - S1 (publishRequested)
     - A1 (childOf)
     - A2 (childOf)
         - A2_1 - Ref:B2_1 (childOf)
    - S2 (referredTo)
     - B1 (New)
     - B2 (referredTo)
         - B2_1 (referredTo)
    */
    private ResolveContext determineResolveContext( final Node nodeToResolve )
    {
        if ( nodeId == null )
        {
            return ResolveContext.requested();
        }
        if ( nodeToResolve.id().equals( nodeId ) )
        {
            return ResolveContext.requested();
        }

        final Node initialNode = doGetById( nodeId, false );

        if ( nodeToResolve.path().toString().contains( initialNode.path().toString() ) )
        {
            return ResolveContext.childOf( nodeId );
        }
        if ( initialNode.path().toString().contains( nodeToResolve.path().toString() ) )
        {
            return ResolveContext.parentFor( nodeId );
        }

        return ResolveContext.referredFrom( nodeId );
    }

    private static class ResolveContext
    {
        private final NodeId contextNodeId;

        private boolean becauseParent = false;

        private boolean becauseChild = false;

        private boolean becauseReferredTo = false;

        private ResolveContext( final boolean becauseChild, final boolean becauseParent, final boolean becauseReferredTo,
                                final NodeId contextNodeId )
        {
            this.becauseChild = becauseChild;
            this.becauseParent = becauseParent;
            this.becauseReferredTo = becauseReferredTo;
            this.contextNodeId = contextNodeId;
        }

        private static ResolveContext parentFor( final NodeId nodeId )
        {
            return new ResolveContext( false, true, false, nodeId );
        }

        private static ResolveContext childOf( final NodeId nodeId )
        {
            return new ResolveContext( true, false, false, nodeId );
        }

        private static ResolveContext referredFrom( final NodeId nodeId )
        {
            return new ResolveContext( false, false, true, nodeId );
        }

        private static ResolveContext requested()
        {
            return new ResolveContext( false, false, false, null );
        }

    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private Branch target;

        private boolean includeChildren = true;

        private NodePath repositoryRoot = NodePath.ROOT;

        private Builder()
        {
        }

        public Builder repositoryRoot( final NodePath repositoryRoot )
        {
            this.repositoryRoot = repositoryRoot;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public ResolveSyncWorkCommand build()
        {
            return new ResolveSyncWorkCommand( this );
        }
    }
}
