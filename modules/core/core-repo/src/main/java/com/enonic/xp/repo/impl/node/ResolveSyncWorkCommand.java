package com.enonic.xp.repo.impl.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.InternalContext;

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

    private final Set<CompareStatus> statusesToStopDependenciesSearch;

    private final Function<NodeIds, NodeIds> filter;

    private final Function<NodeIds, NodeIds> statusFilter;

    private static final Logger LOG = LoggerFactory.getLogger( ResolveSyncWorkCommand.class );

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

        this.statusFilter = statusesToStopDependenciesSearch != null ? nodeIds -> {
            final NodeIds.Builder filteredNodeIds = NodeIds.create();

            final NodeComparisons currentLevelNodeComparisons = CompareNodesCommand.create()
                .nodeIds( nodeIds )
                .target( target )
                .storageService( this.nodeStorageService )
                .build()
                .execute();

            currentLevelNodeComparisons.getComparisons()
                .stream()
                .filter( nodeComparison -> !statusesToStopDependenciesSearch.contains( nodeComparison.getCompareStatus() ) )
                .map( NodeComparison::getNodeId )
                .forEach( filteredNodeIds::add );

            return filteredNodeIds.build();
        } : Function.identity();
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
        final NodeIds.Builder diffAndDependantsBuilder = NodeIds.create();

        final NodeIds initialDiff = getInitialDiff();
        diffAndDependantsBuilder.addAll( initialDiff );

        if ( includeDependencies )
        {
            final NodeIds nodeDependencies = getNodeDependencies( initialDiff );
            diffAndDependantsBuilder.addAll( nodeDependencies );
        }

        final Set<NodeComparison> comparisons = getFilteredComparisons( diffAndDependantsBuilder );

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

        final Stopwatch timer = Stopwatch.createStarted();

        final NodeVersionDiffResult nodesWithVersionDifference = findNodesWithVersionDifference( this.publishRootNode.path() );

        LOG.debug( "Diff-query result in " + timer.stop() );

        final NodeIds nodeIds = nodesWithVersionDifference.getNodesWithDifferences().stream().
            filter( ( nodeId ) -> !this.excludedIds.contains( nodeId ) ).
            collect( NodeIds.collector() );

        return initialDiff.addAll( nodeIds ).
            build();
    }

    private NodeVersionDiffResult findNodesWithVersionDifference( final NodePath nodePath )
    {
        return FindNodesWithVersionDifferenceCommand.create().
            target( target ).
            source( ContextAccessor.current().getBranch() ).
            nodePath( nodePath ).
            excludes( this.excludedIds ).
            searchService( this.nodeSearchService ).
            storageService( this.nodeStorageService ).
            build().
            execute();
    }

    private NodeIds getNodeDependencies( final NodeIds initialDiff )
    {
        return FindNodesDependenciesCommand.create( this ).nodeIds( initialDiff )
            .excludedIds( excludedIds )
            .filter( filter != null ? statusFilter.compose( filter ) : statusFilter )
            .
            build().
            execute();
    }

    private void addNewAndMovedParents( final Set<NodeComparison> comparisons )
    {
        final NodePaths parentPaths = getPathsFromComparisons( comparisons );

        final NodeIds parentIds = getParentIdsFromPaths( parentPaths );

        final NodeIds.Builder filteredParentIdsBuilder = NodeIds.create();
        getFilteredNewAndMovedParentComparisons( parentIds ).
            stream().
            map( NodeComparison::getNodeId ).
            forEach( filteredParentIdsBuilder::add );
        final NodeIds filteredParentIds = filteredParentIdsBuilder.build();

        final NodeIds parentsDependencies = includeDependencies ? getNodeDependencies( filteredParentIds ) : NodeIds.empty();

        final NodeComparisons newComparisonsToConsider = CompareNodesCommand.create().
            nodeIds( NodeIds.create().
                addAll( parentsDependencies ).
                addAll( filteredParentIds ).
                build() ).
            target( this.target ).
            storageService( this.nodeStorageService ).
            build().
            execute();

        final Set<NodeComparison> newAndMoved = getNewAndMoved( newComparisonsToConsider );

        addToResult( NodeComparisons.create().addAll( newAndMoved ).build() );

        if ( !newAndMoved.isEmpty() )
        {
            addNewAndMovedParents( newAndMoved );
        }
    }

    private Set<NodeComparison> getNewAndMoved( final NodeComparisons parentComparisons )
    {
        return parentComparisons.getComparisons().stream().
            filter( comparison -> comparison.getCompareStatus().equals( CompareStatus.NEW ) ||
                comparison.getCompareStatus().equals( CompareStatus.MOVED ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
    }

    private NodeIds getParentIdsFromPaths( final NodePaths parentPaths )
    {
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

    private NodePaths getPathsFromComparisons( final Set<NodeComparison> comparisons )
    {
        final NodePaths.Builder parentPathsBuilder = NodePaths.create();

        for ( final NodeComparison comparison : comparisons )
        {
            addParentPaths( parentPathsBuilder, comparison.getSourcePath() );
        }

        return parentPathsBuilder.build();
    }

    private Set<NodeComparison> getFilteredComparisons( final NodeIds.Builder diffAndDependantsBuilder )
    {
        final NodeComparisons allNodesComparisons = CompareNodesCommand.create().
            target( this.target ).
            nodeIds( diffAndDependantsBuilder.build() ).
            storageService( this.nodeStorageService ).
            build().
            execute();

        return allNodesComparisons.getComparisons().stream().
            filter( comparison -> !( nodeNotChanged( comparison ) || nodeNotInSource( comparison ) ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            filter( comparison -> !this.excludedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
    }

    private Set<NodeComparison> getFilteredNewAndMovedParentComparisons( final NodeIds nodeIds )
    {
        final NodeComparisons allNodesComparisons = CompareNodesCommand.create().
            target( this.target ).
            nodeIds( nodeIds ).
            storageService( this.nodeStorageService ).
            build().
            execute();

        return allNodesComparisons.getComparisons().stream().
            filter( comparison -> nodeMoved( comparison ) || nodeNotInTarget( comparison ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
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


    private void addToResult( final NodeComparison comparison )
    {
        this.result.add( comparison );
        this.processedIds.add( comparison.getNodeId() );
    }

    private void addToResult( final NodeComparisons comparisons )
    {
        this.result.addAll( comparisons.getComparisons() );
        this.processedIds.addAll( comparisons.getNodeIds().getSet() );
    }

    private boolean nodeMoved( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.MOVED;
    }

    private boolean nodeNotInTarget( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.NEW;
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

        private boolean includeDependencies = true;

        private Set<CompareStatus> statusesToStopDependenciesSearch;

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

        public Builder statusesToStopDependenciesSearch( final Set<CompareStatus> statusesToStopDependenciesSearch )
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
