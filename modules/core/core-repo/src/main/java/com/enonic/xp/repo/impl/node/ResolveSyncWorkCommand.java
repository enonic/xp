package com.enonic.xp.repo.impl.node;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final Node publishRootNode;

    private final Set<NodeId> processedIds;

    private final NodeIds excludedIds;

    private final NodeIds.Builder result;

    private boolean allPossibleNodesAreIncluded;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.repositoryRoot = builder.repositoryRoot;
        this.result = NodeIds.create();
        this.processedIds = Sets.newHashSet();
        this.excludedIds = builder.excludedIds;
        this.allPossibleNodesAreIncluded = builder.allPossibleNodesAreIncluded;

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
        getAllPossibleNodesToBePublished();

        return this.result.build();
    }

    private NodeIds getAllPossibleNodesToBePublished()
    {
        final NodeIds.Builder diffAndDependantsBuilder = NodeIds.create();

        final NodeIds initialDiff = getInitialDiff();
        diffAndDependantsBuilder.addAll( initialDiff );
        diffAndDependantsBuilder.addAll( getNodeDependencies( initialDiff ) );

        final Set<NodeComparison> comparisons = getFilteredComparisons( diffAndDependantsBuilder );

        addNewAndMovedParents( comparisons );

        comparisons.forEach( ( comparison -> addToResult( comparison.getNodeId() ) ) );

        markPendingDeleteChildrenForDeletion( comparisons );

        return NodeIds.empty();
    }

    private NodeIds getInitialDiff()
    {
        if ( !includeChildren )
        {
            return NodeIds.from( this.publishRootNode.id() );
        }

        final NodeVersionDiffResult nodesWithVersionDifference = findNodesWithVersionDifference( this.publishRootNode.path() );

        return NodeIds.from( nodesWithVersionDifference.getNodesWithDifferences().stream().
            filter( ( nodeId ) -> !this.excludedIds.contains( nodeId ) ).
            collect( Collectors.toSet() ) );
    }

    private NodeVersionDiffResult findNodesWithVersionDifference( final NodePath nodePath )
    {
        return FindNodesWithVersionDifferenceCommand.create().
            target( target ).
            source( ContextAccessor.current().getBranch() ).
            nodePath( nodePath ).
            excludes( this.excludedIds ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();
    }

    private NodeIds getNodeDependencies( final NodeIds initialDiff )
    {
        final NodeIds dependencies = FindNodesDependenciesCommand.create( this ).
            nodeIds( initialDiff ).
            recursive( true ).
            build().
            execute();

        return NodeIds.from( dependencies.getSet().stream().
            filter( ( nodeId ) -> !this.excludedIds.contains( nodeId ) ).
            collect( Collectors.toSet() ) );
    }

    private void addNewAndMovedParents( final Set<NodeComparison> comparisons )
    {
        final NodePaths.Builder parentPathsBuilder = NodePaths.create();

        for ( final NodeComparison comparison : comparisons )
        {
            addParentPaths( parentPathsBuilder, comparison.getSourceEntry().getNodePath() );
        }

        final NodePaths parentPaths = parentPathsBuilder.build();

        final NodeIds.Builder parentIdBuilder = NodeIds.create();

        for ( final NodePath parent : parentPaths )
        {
            final NodeId parentId = this.storageService.getIdForPath( parent, InternalContext.from( ContextAccessor.current() ) );
            parentIdBuilder.add( parentId );
        }

        final NodeIds parentIds = parentIdBuilder.build();

        final NodeIds parentsDependencies = getNodeDependencies( parentIds );

        doAddNewAndMoved( CompareNodesCommand.create().
            nodeIds( NodeIds.create().
                addAll( parentsDependencies ).
                addAll( parentIds ).
                build() ).
            target( this.target ).
            storageService( this.storageService ).
            build().
            execute() );
    }

    private void doAddNewAndMoved( final NodeComparisons parentComparisons )
    {
        parentComparisons.getNodeComparisons().stream().
            filter( ( comparison -> comparison.getCompareStatus().equals( CompareStatus.NEW ) ||
                comparison.getCompareStatus().equals( CompareStatus.MOVED ) ) ).
            forEach( ( comparison ) -> addToResult( comparison.getNodeId() ) );
    }

    private void markPendingDeleteChildrenForDeletion( final Set<NodeComparison> comparisons )
    {
        comparisons.stream().
            filter( comparison -> comparison.getCompareStatus().equals( CompareStatus.PENDING_DELETE ) ).
            forEach( comparison -> markChildrenForDeletion( comparison.getNodeId() ) );
    }

    private Set<NodeComparison> getFilteredComparisons( final NodeIds.Builder diffAndDependantsBuilder )
    {
        final NodeComparisons allNodesComparisons = CompareNodesCommand.create().
            target( this.target ).
            nodeIds( NodeIds.from( diffAndDependantsBuilder.build() ) ).
            storageService( this.storageService ).
            build().
            execute();

        return allNodesComparisons.getNodeComparisons().stream().
            filter( comparison -> !( nodeNotChanged( comparison ) || nodeNotInSource( comparison ) ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            filter( comparison -> !this.excludedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
    }

    private void markChildrenForDeletion( final NodeId nodeId )
    {
        final NodeIds childrenToBeDeleted = FindNodeIdsByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                size( SearchService.GET_ALL_SIZE_FLAG ).
                parentId( nodeId ).
                childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
                recursive( true ).
                build() ).
            build().
            execute();

        addToResult( childrenToBeDeleted );
    }

    private void addParentPaths( final NodePaths.Builder parentPathsBuilder, final NodePath path )
    {
        if ( path.isRoot() )
        {
            return;
        }

        for ( NodePath parentPath : path.getParentPaths() )
        {
            if ( parentPath.isRoot() )
            {
                return;
            }

            parentPathsBuilder.addNodePath( parentPath );
        }
    }


    private void addToResult( final NodeId nodeId )
    {
        this.result.add( nodeId );
        this.processedIds.add( nodeId );
    }

    private void addToResult( final NodeIds nodeIds )
    {
        this.result.addAll( nodeIds );
        this.processedIds.addAll( nodeIds.getSet() );
    }

    private boolean nodeNotInSource( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.NEW_TARGET;
    }

    private boolean nodeNotChanged( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.EQUAL;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodeIds excludedIds = NodeIds.empty();

        private Branch target;

        private boolean includeChildren = true;

        private NodePath repositoryRoot = NodePath.ROOT;

        private boolean allPossibleNodesAreIncluded = false;

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

        public Builder excludedNodeIds( final NodeIds excludedNodeIds )
        {
            if ( excludedNodeIds != null )
            {
                this.excludedIds = excludedNodeIds;
            }
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

        public Builder allPossibleNodesAreIncluded( final boolean allPossibleNodesAreIncluded )
        {
            this.allPossibleNodesAreIncluded = allPossibleNodesAreIncluded;
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