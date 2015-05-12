package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
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

        for ( final NodeId nodeId : diff.getNodesWithDifferences() )
        {
            resolveDiffWithNodeIdAsInput( nodeId, ResolveContext.requested() );
        }

        return resultBuilder.build();
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

    private void resolveDiffWithNodeIdAsInput( final NodeId nodeId, final ResolveContext resolveContext )
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
            final Node thisParentNode = doGetByPath( node.parentPath(), false );

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiffWithNodeAsInput( thisParentNode, ResolveContext.parentFor( node.id() ) );
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
                    resolveDiffWithNodeIdAsInput( referredNodeId, ResolveContext.referredFrom( node.id() ) );
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

    private void resolveAndAddDiffResult( final ResolveContext resolveContext, final NodeComparison comparison )
    {
        final NodeId nodeId = comparison.getNodeId();

        if ( comparison.getCompareStatus().isConflict() )
        {
            resultBuilder.conflict( nodeId );
        }
        else if ( nodeId.equals( this.publishRootNode.id() ) )
        {
            addRequestedOrChild( nodeId );
        }
        else
        {
            if ( comparison.getCompareStatus() == CompareStatus.PENDING_DELETE )
            {
                if ( resolveContext.becauseReferredTo )
                {
                    resultBuilder.deleteReferredFrom( comparison.getNodeId(), resolveContext.contextNodeId, this.publishRootNode.id() );
                }
                else if ( resolveContext.becauseParent )
                {
                    resultBuilder.deleteParentFor( comparison.getNodeId(), resolveContext.contextNodeId, this.publishRootNode.id() );
                }
                else if ( resolveContext.becauseChild )
                {
                    resultBuilder.deleteChildOf( comparison.getNodeId(), resolveContext.contextNodeId, this.publishRootNode.id() );
                }
                else
                {
                    resultBuilder.deleteRequested( comparison.getNodeId(), this.publishRootNode.id() );
                }
            }
            else if ( resolveContext.becauseReferredTo )
            {
                resultBuilder.publishReferredFrom( comparison.getNodeId(), resolveContext.contextNodeId, this.publishRootNode.id() );
            }
            else if ( resolveContext.becauseParent )
            {
                resultBuilder.publishParentFor( comparison.getNodeId(), resolveContext.contextNodeId, this.publishRootNode.id() );
            }
            else
            {
                addRequestedOrChild( comparison.getNodeId() );
            }
        }
    }


    public void addRequestedOrChild( final NodeId nodeId )
    {
        if ( nodeId.equals( this.publishRootNode.id() ) )
        {
            this.resultBuilder.publishRequested( nodeId, this.publishRootNode.id() );
        }
        else
        {
            final Node node = doGetById( nodeId, false );

            final NodePath parentPath = node.parentPath();

            final Node parentNode = doGetByPath( parentPath, false );

            this.resultBuilder.publishChildOf( nodeId, parentNode.id(), this.publishRootNode.id() );
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
