package com.enonic.xp.repo.impl.node;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;

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

    private static final Logger LOG = LoggerFactory.getLogger( ResolveSyncWorkCommand.class );

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.result = ResolveSyncWorkResult.create();
        this.processedIds = Sets.newHashSet();
        this.excludedIds = builder.excludedIds;
        this.includeDependencies = builder.includeDependencies;

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
        final Stopwatch timer = Stopwatch.createStarted();
        getAllPossibleNodesToBePublished();
        timer.stop();

        final ResolveSyncWorkResult result = this.result.build();

        final String speed = timer.elapsed( TimeUnit.SECONDS ) == 0 ? "N/A" : result.getSize() / timer.elapsed( TimeUnit.SECONDS ) + "/s";

        LOG.debug( "ResolveSyncWorkCommand: " + timer + ", found " + result.getSize() + ", speed: " + speed );
        return result;
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

        comparisons.forEach( ( this::addToResult ) );

        markPendingDeleteChildrenForDeletion( comparisons );
    }

    private NodeIds getInitialDiff()
    {
        if ( !includeChildren && !forceCheckChildren() )
        {
            return NodeIds.from( this.publishRootNode.id() );
        }

        final Stopwatch timer = Stopwatch.createStarted();

        final NodeVersionDiffResult nodesWithVersionDifference = findNodesWithVersionDifference( this.publishRootNode.path() );

        LOG.debug( "Diff-query result in " + timer.stop() );

        return NodeIds.from( nodesWithVersionDifference.getNodesWithDifferences().stream().
            filter( ( nodeId ) -> !this.excludedIds.contains( nodeId ) ).
            collect( Collectors.toSet() ) );
    }

    private boolean forceCheckChildren()
    {
        final NodeComparison rootNodeStatus = CompareNodeCommand.create().
            nodeId( this.publishRootNode.id() ).
            target( this.target ).
            storageService( this.storageService ).
            build().
            execute();

        return rootNodeStatus.getCompareStatus().equals( CompareStatus.PENDING_DELETE );
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

        final NodeIds filtered = NodeIds.from( dependencies.getSet().stream().
            filter( ( nodeId ) -> !this.excludedIds.contains( nodeId ) ).
            collect( Collectors.toSet() ) );

        return filtered;
    }

    private void addNewAndMovedParents( final Set<NodeComparison> comparisons )
    {
        final NodePaths parentPaths = getPathsFromComparisons( comparisons );

        final NodeIds parentIds = getParentIdsFromPaths( parentPaths );

        final NodeIds parentsDependencies = includeDependencies ? getNodeDependencies( parentIds ) : NodeIds.empty();

        final NodeComparisons newComparisonsToConsider = CompareNodesCommand.create().
            nodeIds( NodeIds.create().
                addAll( parentsDependencies ).
                addAll( parentIds ).
                build() ).
            target( this.target ).
            storageService( this.storageService ).
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
            filter( ( comparison -> comparison.getCompareStatus().equals( CompareStatus.NEW ) ||
                comparison.getCompareStatus().equals( CompareStatus.MOVED ) ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
    }

    private NodeIds getParentIdsFromPaths( final NodePaths parentPaths )
    {
        final NodeIds.Builder parentIdBuilder = NodeIds.create();

        for ( final NodePath parent : parentPaths )
        {
            final NodeId parentId = this.storageService.getIdForPath( parent, InternalContext.from( ContextAccessor.current() ) );

            if ( parentId == null )
            {
                throw new NodeNotFoundException( "Cannot find parent with path [" + parent + "]" );
            }

            parentIdBuilder.add( parentId );
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

    private void markPendingDeleteChildrenForDeletion( final Set<NodeComparison> comparisons )
    {
        comparisons.stream().
            filter( comparison -> comparison.getCompareStatus().equals( CompareStatus.PENDING_DELETE ) ).
            forEach( this::markChildrenForDeletion );
    }

    private Set<NodeComparison> getFilteredComparisons( final NodeIds.Builder diffAndDependantsBuilder )
    {
        final NodeComparisons allNodesComparisons = CompareNodesCommand.create().
            target( this.target ).
            nodeIds( NodeIds.from( diffAndDependantsBuilder.build() ) ).
            storageService( this.storageService ).
            build().
            execute();

        return allNodesComparisons.getComparisons().stream().
            filter( comparison -> !( nodeNotChanged( comparison ) || nodeNotInSource( comparison ) ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            filter( comparison -> !this.excludedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );
    }

    private void markChildrenForDeletion( final NodeComparison comparison )
    {
        final FindNodesByParentResult result = FindNodeIdsByParentCommand.create( this ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            parentId( comparison.getNodeId() ).
            childOrder( ChildOrder.from( NodeIndexPath.PATH + " asc" ) ).
            recursive( true ).
            build().
            execute();

        final NodeBranchEntries brancEntries =
            this.storageService.getBranchNodeVersions( result.getNodeIds(), false, InternalContext.from( ContextAccessor.current() ) );

        addToResult( NodeComparisons.create().
            addAll( brancEntries.stream().
                map( ( branchEntry ) -> new NodeComparison( branchEntry, branchEntry, CompareStatus.PENDING_DELETE ) ).
                collect( Collectors.toSet() ) ).
            build() );
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