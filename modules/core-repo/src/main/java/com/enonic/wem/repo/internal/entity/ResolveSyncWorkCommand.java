package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.branch.Branch;

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
            resolveDiffWithNodeId( nodeId, ResolveContext.requested() );
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

    private void resolveDiffWithNode( final Node node, final ResolveContext resolveContext )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node, node.id(), resolveContext );
    }

    private void resolveDiffWithNodeId( final NodeId nodeId, final ResolveContext resolveContext )
    {
        if ( isProcessed( nodeId ) )
        {
            return;
        }

        this.processedIds.add( nodeId );

        final Node node = doGetById( nodeId, false );

        doResolveDiff( node, nodeId, resolveContext );
    }

    private void doResolveDiff( final Node node, final NodeId nodeId, final ResolveContext resolveContext )
    {
        final NodeComparison comparison = getNodeComparison( nodeId );

        if ( nodeNotChanged( comparison ) )
        {
            return;
        }

        addResult( comparison, resolveContext );

        if ( !allPossibleNodesAreIncluded )
        {
            ensureThatParentExists( node );
            includeReferences( node );
        }
    }

    private boolean nodeNotChanged( final NodeComparison comparison )
    {
        return comparison.getCompareStatus().getStatus().equals( CompareStatus.Status.EQUAL );
    }

    private void ensureThatParentExists( final Node node )
    {
        if ( !node.parentPath().equals( NodePath.ROOT ) )
        {
            final Node thisParentNode = doGetByPath( node.parentPath(), false );

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiffWithNode( thisParentNode, ResolveContext.parentFor( node.id() ) );
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
                    resolveDiffWithNodeId( referredNodeId, ResolveContext.referredFrom( node.id() ) );
                }
            }
        }
    }

    private boolean shouldBeResolvedDiffFor( final NodeComparison nodeComparison )
    {
        final CompareStatus.Status status = nodeComparison.getCompareStatus().getStatus();
        return status.equals( CompareStatus.Status.NEW ) || status.equals( CompareStatus.Status.MOVED );
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

    private void addResult( final NodeComparison comparison, final ResolveContext resolveContext )
    {
        final NodeId nodeId = comparison.getNodeId();

        if ( comparison.getCompareStatus().isConflict() )
        {
            resultBuilder.conflict( nodeId );
        }
        else
        {
            if ( comparison.getCompareStatus().getStatus().equals( CompareStatus.Status.PENDING_DELETE ) )
            {
                resultBuilder.addDelete( nodeId );
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
                resultBuilder.publishRequested( comparison.getNodeId() );
            }
        }
    }

    private static class ResolveContext
    {
        private boolean becauseParent = false;

        private boolean becauseReferredTo = false;

        private final NodeId contextNodeId;

        private ResolveContext( final boolean becauseParent, final boolean becauseReferredTo, final NodeId contextNodeId )
        {
            this.becauseParent = becauseParent;
            this.becauseReferredTo = becauseReferredTo;
            this.contextNodeId = contextNodeId;
        }

        private static ResolveContext parentFor( final NodeId nodeId )
        {
            return new ResolveContext( true, false, nodeId );
        }

        private static ResolveContext referredFrom( final NodeId nodeId )
        {
            return new ResolveContext( false, true, nodeId );
        }

        private static ResolveContext requested()
        {
            return new ResolveContext( false, false, null );
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
