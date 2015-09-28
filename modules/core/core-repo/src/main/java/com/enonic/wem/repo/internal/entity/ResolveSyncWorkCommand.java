package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.Nodes;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    //private final NodeId nodeId;

    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final Node publishRootNode;

    private final Set<NodeId> processedIds;

    private boolean allPossibleNodesAreIncluded;

    private final NodeIds.Builder result;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.repositoryRoot = builder.repositoryRoot;
        this.result = NodeIds.create();
        this.processedIds = Sets.newHashSet();
        this.allPossibleNodesAreIncluded = false;

        final Node publishRootNode = doGetById( builder.nodeId );

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

    public NodeIds execute()
    {
        final NodeVersionDiffResult diff = getInitialDiff();

        final Nodes nodes = GetNodesByIdsCommand.create( this ).
            ids( diff.getNodesWithDifferences() ).
            searchService( this.searchService ).
            build().
            execute();

        for ( final Node node : nodes )
        {
            resolveDiffWithNodeIdAsInput( node );
        }

        return result.build();
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
            query( FindNodesWithVersionDifferenceParams.create().
                target( target ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( this.publishRootNode.path() ).
                size( SearchService.GET_ALL_SIZE_FLAG ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private void resolveDiffWithNodeAsInput( final Node node )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node );
    }

    private void resolveDiffWithNodeIdAsInput( final Node node )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node );

    }

    private void doResolveDiff( final Node node )
    {
        final NodeComparison comparison = getNodeComparison( node.id() );

        if ( nodeNotChanged( comparison ) )
        {
            return;
        }

        this.result.add( node.id() );

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
                build().
                execute();

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiffWithNodeAsInput( thisParentNode );
            }
        }
    }

    private void includeReferences( final Node node )
    {
        final ImmutableList<Property> references = node.data().getProperties( ValueTypes.REFERENCE );

        final NodeIds.Builder referredNodeIds = NodeIds.create();

        references.stream().filter( Property::hasNotNullValue ).forEach( reference -> {
            referredNodeIds.add( reference.getReference().getNodeId() );
        } );

        final Nodes referredNodes = GetNodesByIdsCommand.create( this ).
            ids( referredNodeIds.build() ).
            build().
            execute();

        for ( final Node referredNode : referredNodes )
        {
            if ( !this.processedIds.contains( referredNode.id() ) )
            {
                resolveDiffWithNodeIdAsInput( referredNode );
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
            storageService( this.storageService ).
            nodeId( nodeId ).
            build().
            execute();
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

        void validate()
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