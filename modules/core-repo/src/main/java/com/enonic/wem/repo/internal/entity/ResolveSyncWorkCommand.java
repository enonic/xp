package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.ResolveSyncWorkResult;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    //private final NodeId nodeId;

    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final Node publishRootNode;

    private final ResolveSyncWorkResult.Builder resultBuilder;

    private final Set<NodeId> processedIds;

    private boolean allPossibleNodesAreIncluded;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.repositoryRoot = builder.repositoryRoot;
        this.resultBuilder = ResolveSyncWorkResult.create();
        this.processedIds = Sets.newHashSet();
        this.allPossibleNodesAreIncluded = false;

        final Node publishRootNode = doGetById( builder.nodeId, false );

        if ( publishRootNode == null )
        {
            throw new NodeNotFoundException( "Root node for publishing not found. Id: " + builder.nodeId );
        }

        this.publishRootNode = publishRootNode;

        if ( this.repositoryRoot.equals( this.publishRootNode.path() ) )
        {
            this.allPossibleNodesAreIncluded = true;
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ResolveSyncWorkResult execute()
    {
        final NodeVersionDiffResult diff = getInitialDiff();

        final Nodes nodes = GetNodesByIdsCommand.create( this ).
            ids( diff.getNodesWithDifferences() ).
            resolveHasChild( false ).
            build().
            execute();

        for ( final Node node : nodes )
        {
            resolveDiffWithNodeIdAsInput( node, ResolveContext.requested() );
        }

        return resultBuilder.setInitialReasonNodeId( this.publishRootNode.id() ).build();
    }

    private NodeVersionDiffResult getInitialDiff()
    {
        if ( !includeChildren )
        {
            return NodeVersionDiffResult.create().
                add( this.publishRootNode.id() ).
                build();
        }

        return FindNodesWithVersionDifferenceCommand.create().
            versionService( this.versionService ).
            query( NodeVersionDiffQuery.create().
                target( target ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( this.publishRootNode.path() ).
                build() ).
            build().
            execute();
    }

    private void resolveDiffWithNodeAsInput( final Node node, final ResolveContext resolveContext )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node, resolveContext );
    }

    private void resolveDiffWithNodeIdAsInput( final Node node, final ResolveContext resolveContext )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        if ( node == null )
        {
            // does not exist in source workspace, skip
        }
        else
        {
            doResolveDiff( node, resolveContext );
        }
    }

    private void doResolveDiff( final Node node, final ResolveContext resolveContext )
    {
        final NodeComparison comparison = getNodeComparison( node.id() );

        if ( nodeNotChanged( comparison ) )
        {
            return;
        }

        resolveAndAddDiffResult( resolveContext, comparison );

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
        if ( !node.isRoot() && !node.parentPath().equals( NodePath.ROOT ) )
        {
            final Node thisParentNode = GetNodeByPathCommand.create( this ).
                nodePath( node.parentPath() ).
                resolveHasChild( false ).
                build().
                execute();

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiffWithNodeAsInput( thisParentNode, ResolveContext.parentFor( node.id() ) );
            }
        }
    }

    private void includeReferences( final Node node )
    {
        final ImmutableList<Property> references = node.data().getProperties( ValueTypes.REFERENCE );

        final NodeIds.Builder referredNodeIds = NodeIds.create();

        for ( final Property reference : references )
        {
            if ( reference.hasNotNullValue() )
            {
                referredNodeIds.add( reference.getReference().getNodeId() );
            }
        }

        final Nodes referredNodes = GetNodesByIdsCommand.create( this ).
            resolveHasChild( false ).
            ids( referredNodeIds.build() ).
            build().
            execute();

        for ( final Node referredNode : referredNodes )
        {

            if ( !this.processedIds.contains( referredNode.id() ) )
            {
                resolveDiffWithNodeIdAsInput( referredNode, ResolveContext.referredFrom( node.id() ) );
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

    private void resolveAndAddDiffResult( final ResolveContext resolveContext, final NodeComparison comparison )
    {
        final NodeId nodeId = comparison.getNodeId();

        if ( comparison.getCompareStatus().isConflict() )
        {
            resultBuilder.conflict( nodeId );
        }
        else
        {
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
                else
                {
                    addRequestedOrChild( comparison.getNodeId(), true );
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
            else
            {
                addRequestedOrChild( comparison.getNodeId(), false );
            }
        }
    }


    public void addRequestedOrChild( final NodeId nodeId, boolean isDelete )
    {
        if ( nodeId.equals( this.publishRootNode.id() ) )
        {
            if ( isDelete )
            {
                this.resultBuilder.deleteRequested( nodeId );
            }
            else
            {
                this.resultBuilder.publishRequested( nodeId );
            }
        }
        else
        {
            final Node node = doGetById( nodeId, false );

            final NodePath parentPath = node.parentPath();

            final Node parentNode = GetNodeByPathCommand.create( this ).
                nodePath( parentPath ).
                resolveHasChild( false ).
                build().
                execute();

            if ( isDelete )
            {
                this.resultBuilder.deleteChildOf( nodeId, parentNode.id() );
            }
            else
            {
                this.resultBuilder.publishChildOf( nodeId, parentNode.id() );
            }
        }
    }

    public static class ResolveContext
    {
        private final NodeId contextNodeId;

        private boolean becauseParent = false;

        private boolean becauseChild = false;

        private boolean becauseReferredTo = false;

        private boolean becauseRequested = false;

        private ResolveContext( final boolean becauseChild, final boolean becauseParent, final boolean becauseReferredTo,
                                final boolean becauseRequested, final NodeId contextNodeId )
        {
            this.becauseChild = becauseChild;
            this.becauseParent = becauseParent;
            this.becauseReferredTo = becauseReferredTo;
            this.contextNodeId = contextNodeId;
            this.becauseRequested = becauseRequested;
        }

        static ResolveContext parentFor( final NodeId nodeId )
        {
            return new ResolveContext( false, true, false, false, nodeId );
        }

        static ResolveContext childOf( final NodeId nodeId )
        {
            return new ResolveContext( true, false, false, false, nodeId );
        }

        static ResolveContext referredFrom( final NodeId nodeId )
        {
            return new ResolveContext( false, false, true, false, nodeId );
        }

        static ResolveContext requested()
        {
            return new ResolveContext( false, false, false, true, null );
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

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId, "nodeId must be provided" );
            Preconditions.checkNotNull( target, "target branch must be provided" );
        }

        public ResolveSyncWorkCommand build()
        {
            validate();
            return new ResolveSyncWorkCommand( this );
        }
    }

}