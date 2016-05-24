package com.enonic.xp.repo.impl.node;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
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
    private static final Logger LOG = LoggerFactory.getLogger( ResolveSyncWorkCommand.class );

    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final Node publishRootNode;

    private final Set<NodeId> processedIds;

    private final NodeIds excludedIds;

    private final NodeIds.Builder result;

    private int parentTimer = 0;

    private int resolveTimer = 0;

    private Map<NodeId, Integer> processedTimes = Maps.newHashMap();

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
        getAllPossibleNodesToPublish2();

        return this.result.build();
    }

    private NodeIds getAllPossibleNodesToPublish2()
    {
        final Stopwatch allPossibleTimer = Stopwatch.createStarted();

        final NodeIds.Builder diffAndDependantsBuilder = NodeIds.create();

        final NodeIds initialDiff = getInitialDiff().getNodesWithDifferences();
        diffAndDependantsBuilder.addAll( initialDiff );

        final Stopwatch dependenciesTimer = Stopwatch.createStarted();
        diffAndDependantsBuilder.addAll( ResolveNodesDependenciesCommand.create( this ).
            nodeIds( initialDiff ).
            recursive( true ).
            build().
            execute() );
        System.out.println( "DependenciesTimer: " + dependenciesTimer.stop() );

        final Stopwatch compareTimer = Stopwatch.createStarted();
        final NodeComparisons allNodesComparisons = CompareNodesCommand.create().
            target( this.target ).
            nodeIds( NodeIds.from( diffAndDependantsBuilder.build() ) ).
            storageService( this.storageService ).
            build().
            execute();
        System.out.println( "CompareTimer: " + compareTimer.stop() );

        final Set<NodeComparison> comparisons = allNodesComparisons.getNodeComparisons().stream().
            filter( comparison -> !( nodeNotChanged( comparison ) || nodeNotInSource( comparison ) ) ).
            filter( comparison -> !this.processedIds.contains( comparison.getNodeId() ) ).
            filter( comparison -> !this.excludedIds.contains( comparison.getNodeId() ) ).
            collect( Collectors.toSet() );

        final NodeComparisons parentComparisons = getParentCompares( comparisons );

        comparisons.forEach( ( comparison -> newResolveStuff( comparison.getNodeId() ) ) );

        comparisons.stream().
            filter( comparison -> comparison.getCompareStatus().equals( CompareStatus.PENDING_DELETE ) ).
            forEach( comparison -> markChildrenForDeletion( comparison.getNodeId() ) );

        // Parent not in the diff result, but must be published since they are new
        parentComparisons.getNodeComparisons().stream().
            filter( ( comparison -> comparison.getCompareStatus().equals( CompareStatus.NEW ) ||
                comparison.getCompareStatus().equals( CompareStatus.MOVED ) ) ).
            forEach( ( comparison ) -> newResolveStuff( comparison.getNodeId() ) );

        System.out.println( "getAllPossibleNodesToPublish2: " + allPossibleTimer.stop() );

        return NodeIds.empty();
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

        this.result.addAll( childrenToBeDeleted );
    }

    private NodeComparisons getParentCompares( final Set<NodeComparison> comparisons )
    {
        final NodePaths.Builder parentPathsBuilder = NodePaths.create();

        for ( final NodeComparison comparison : comparisons )
        {
            addParentPaths( parentPathsBuilder, comparison.getSourceEntry().getNodePath() );
        }

        final NodePaths parentPaths = parentPathsBuilder.build();

        final NodeIds.Builder parentIds = NodeIds.create();

        for ( final NodePath parent : parentPaths )
        {
            final NodeId parentId = this.storageService.getIdForPath( parent, InternalContext.from( ContextAccessor.current() ) );
            parentIds.add( parentId );
        }

        return CompareNodesCommand.create().
            nodeIds( parentIds.build() ).
            target( this.target ).
            storageService( this.storageService ).
            build().
            execute();
    }


    private void addParentPaths( final NodePaths.Builder parentPathsBuilder, final NodePath path )
    {
        for ( NodePath parentPath : path.getParentPaths() )
        {
            if ( parentPath.isRoot() || parentPath.equals( this.publishRootNode.path() ) )
            {
                return;
            }

            parentPathsBuilder.addNodePath( parentPath );
        }
    }

    private NodeVersionDiffResult getInitialDiff()
    {
        final Stopwatch initialDiff = Stopwatch.createStarted();

        if ( !includeChildren )
        {
            return NodeVersionDiffResult.create().
                add( this.publishRootNode.id() ).
                build();
        }

        final NodeVersionDiffResult nodesWithVersionDifference = findNodesWithVersionDifference( this.publishRootNode.path() );

        System.out.println( "GetInitialDiff: " + initialDiff.stop().toString() );

        return nodesWithVersionDifference;
    }

    private NodeVersionDiffResult findNodesWithVersionDifference( final NodePath nodePath )
    {
        return FindNodesWithVersionDifferenceCommand.create().
            query( FindNodesWithVersionDifferenceParams.create().
                target( target ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( nodePath ).
                size( SearchService.GET_ALL_SIZE_FLAG ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();
    }


    private void newResolveStuff( final NodeId nodeId )
    {
        this.result.add( nodeId );
        this.processedIds.add( nodeId );
    }

    private boolean nodeNotInSource( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.NEW_TARGET;
    }

    private boolean nodeNotChanged( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.EQUAL;
    }

    private boolean nodePendingDelete( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.PENDING_DELETE;
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