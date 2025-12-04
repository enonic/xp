package com.enonic.xp.repo.impl.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private final Node publishRootNode;

    private final Set<NodeId> processedIds;

    private final NodeIds excludedIds;

    private final ResolveSyncWorkResult.Builder result;

    private final Set<NodeCompareStatus> statusesToStopDependenciesSearch;

    private final Function<NodeIds, NodeIds> filter;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.result = ResolveSyncWorkResult.create();
        this.processedIds = new HashSet<>();
        this.excludedIds = builder.excludedIds;
        this.includeDependencies = builder.includeDependencies;
        this.statusesToStopDependenciesSearch = builder.statusesToStopDependenciesSearch;
        this.filter = builder.filter;

        final Node publishRootNode = doGetById( builder.nodeId );

        if ( publishRootNode == null )
        {
            throw new NodeNotFoundException( "Root node for publishing not found. Id: " + builder.nodeId );
        }

        this.publishRootNode = publishRootNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ResolveSyncWorkResult execute()
    {
        refresh( RefreshMode.ALL );

        getAllPossibleNodesToBePublished();
        return this.result.build();
    }

    private void getAllPossibleNodesToBePublished()
    {
        final NodeIds initialDiff = getInitialDiff();

        final NodeIds diffAndDependants =
            includeDependencies ? NodeIds.create().addAll( getNodeDependencies( initialDiff ) ).addAll( initialDiff ).build() : initialDiff;

        final Set<NodeComparison> comparisons = getComparisons( diffAndDependants ).getComparisons()
            .stream()
            .filter( comparison -> comparison.getCompareStatus() != NodeCompareStatus.EQUAL &&
                comparison.getCompareStatus() != NodeCompareStatus.NEW_TARGET )
            .filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) )
            .filter( comparison -> !this.excludedIds.contains( comparison.getNodeId() ) )
            .collect( Collectors.toSet() );

        addNewAndMovedParents( comparisons );

        comparisons.forEach( this::addToResult );
    }

    private NodeIds getInitialDiff()
    {
        final NodeIds.Builder initialDiff = NodeIds.create().add( this.publishRootNode.id() );

        if ( !includeChildren )
        {
            return initialDiff.build();
        }

        final NodeVersionDiffResult nodesWithVersionDifference = FindNodesWithVersionDifferenceCommand.create()
            .target( target )
            .source( ContextAccessor.current().getBranch() )
            .nodePath( this.publishRootNode.path() )
            .excludes( this.excludedIds )
            .searchService( this.nodeSearchService )
            .storageService( this.nodeStorageService )
            .build()
            .execute();

        nodesWithVersionDifference.getNodesWithDifferences()
            .stream()
            .filter( Predicate.not( excludedIds::contains ) )
            .forEach( initialDiff::add );

        return initialDiff.build();
    }

    private NodeIds getNodeDependencies( final NodeIds nodeIds )
    {
        final Function<NodeIds, NodeIds> statusFilter = statusesToStopDependenciesSearch != null ? n -> getComparisons( n ).getComparisons()
            .stream()
            .filter( nodeComparison -> !statusesToStopDependenciesSearch.contains( nodeComparison.getCompareStatus() ) )
            .map( NodeComparison::getNodeId )
            .collect( NodeIds.collector() ) : Function.identity();

        return FindNodesDependenciesCommand.create( this )
            .nodeIds( nodeIds )
            .excludedIds( excludedIds )
            .filter( filter != null ? statusFilter.compose( filter ) : statusFilter )
            .build()
            .execute();
    }

    private void addNewAndMovedParents( final Set<NodeComparison> comparisons )
    {
        final NodeIds parentIds =
            getParentIdsForPaths( comparisons.stream().map( NodeComparison::getSourcePath ).collect( Collectors.toSet() ) );

        final NodeIds filteredParentIds =
            filterNewAndMoved( parentIds ).stream().map( NodeComparison::getNodeId ).collect( NodeIds.collector() );

        final NodeIds parentsAndDependencies = includeDependencies
            ? NodeIds.create().addAll( getNodeDependencies( filteredParentIds ) ).addAll( filteredParentIds ).build()
            : filteredParentIds;

        final Set<NodeComparison> newAndMoved = filterNewAndMoved( parentsAndDependencies );

        newAndMoved.forEach( this::addToResult );

        if ( !newAndMoved.isEmpty() )
        {
            addNewAndMovedParents( newAndMoved );
        }
    }

    private NodePaths parentPaths( final Set<NodePath> comparisons )
    {
        final NodePaths.Builder parentPathsBuilder = NodePaths.create();

        for ( final NodePath path : comparisons )
        {
            if ( !path.isRoot() )
            {
                for ( NodePath parentPath : path.getParentPaths() )
                {
                    if ( parentPath.isRoot() )
                    {
                        break;
                    }

                    parentPathsBuilder.addNodePath( parentPath );
                }
            }

        }

        return parentPathsBuilder.build();
    }

    private NodeIds getParentIdsForPaths( final Set<NodePath> paths )
    {
        final NodePaths parentPaths = parentPaths( paths );
        final NodeIds.Builder parentIdBuilder = NodeIds.create();

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
        for ( final NodePath parent : parentPaths )
        {
            final NodeBranchEntry parentNodeBranchEntry = this.nodeStorageService.getBranchNodeVersion( parent, internalContext );

            if ( parentNodeBranchEntry == null )
            {
                throw new NodeNotFoundException( "Cannot find parent with path [" + parent + "]" );
            }

            parentIdBuilder.add( parentNodeBranchEntry.getNodeId() );
        }

        return parentIdBuilder.build();
    }

    private Set<NodeComparison> filterNewAndMoved( final NodeIds nodeIds )
    {
        return getComparisons( nodeIds ).getComparisons()
            .stream()
            .filter( comparison -> comparison.getCompareStatus() == NodeCompareStatus.MOVED ||
                comparison.getCompareStatus() == NodeCompareStatus.NEW )
            .filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) )
            .collect( Collectors.toSet() );
    }

    private NodeComparisons getComparisons( final NodeIds nodeIds )
    {
        return CompareNodesCommand.create()
            .target( this.target )
            .nodeIds( nodeIds )
            .storageService( this.nodeStorageService )
            .build()
            .execute();
    }

    private void addToResult( final NodeComparison comparison )
    {
        this.result.add( comparison );
        this.processedIds.add( comparison.getNodeId() );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodeIds excludedIds = NodeIds.empty();

        private Branch target;

        private boolean includeChildren = true;

        private boolean includeDependencies = true;

        private Set<NodeCompareStatus> statusesToStopDependenciesSearch;

        private Function<NodeIds, NodeIds> filter;

        private Builder()
        {
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

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder filter( final Function<NodeIds, NodeIds> filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder statusesToStopDependenciesSearch( final Set<NodeCompareStatus> statusesToStopDependenciesSearch )
        {
            this.statusesToStopDependenciesSearch = statusesToStopDependenciesSearch;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( nodeId, "nodeId is required" );
            Objects.requireNonNull( target, "target branch is required" );
        }

        public ResolveSyncWorkCommand build()
        {
            validate();
            return new ResolveSyncWorkCommand( this );
        }
    }

}
