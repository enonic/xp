package com.enonic.xp.repo.impl.node;

import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodePath nodePath;

    private final Branch source;

    private final Branch target;

    private final NodeIds excludes;

    private final NodeSearchService nodeSearchService;

    private final NodeStorageService nodeStorageService;

    private final NodeStore nodeStore;

    private static final int BATCH_SIZE = 20_000;

    private FindNodesWithVersionDifferenceCommand( final Builder builder )
    {
        nodePath = builder.nodePath;
        source = builder.source;
        target = builder.target;
        nodeSearchService = builder.nodeSearchService;
        this.nodeStorageService = builder.nodeStorageService;
        this.nodeStore = builder.nodeStore;
        this.excludes = builder.excludes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionDiffResult execute()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        final NodePaths excludeEntries = excludes.isEmpty()
            ? NodePaths.empty()
            : this.nodeStorageService.getNodeBranchEntries( excludes, context )
                .stream()
                .map( NodeBranchEntry::getNodePath )
                .collect( NodePaths.collector() );

        if ( this.nodeStore.supportsVersionQueries() )
        {
            final List<String> nodeIds = this.nodeStore.diffBranches( context.getRepositoryId(), source, target,
                                                                       nodePath == null || nodePath.isRoot() ? null : nodePath.toString(),
                                                                       excludeEntries.stream().map( NodePath::toString ).toList(), -1 );

            final NodeVersionDiffResult.Builder result = NodeVersionDiffResult.create().totalHits( nodeIds.size() );
            nodeIds.forEach( nodeId -> result.add( NodeId.from( nodeId ) ) );
            return result.build();
        }

        final SearchResult result = this.nodeSearchService.query( NodeVersionDiffQuery.create()
                                                                      .source( source )
                                                                      .target( target )
                                                                      .nodePath( nodePath )
                                                                      .excludes( excludeEntries )
                                                                      .returnFields( ReturnFields.from( VersionIndexPath.NODE_ID ) )
                                                                      .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                      .batchSize( BATCH_SIZE )
                                                                      .build(), context.getRepositoryId() );

        return NodeVersionDiffResultFactory.create( result );
    }

    public static final class Builder
    {
        private NodeSearchService nodeSearchService;

        private NodeStorageService nodeStorageService;

        private NodeStore nodeStore;

        private NodePath nodePath;

        private Branch source;

        private Branch target;

        private NodeIds excludes = NodeIds.empty();

        private Builder()
        {
        }

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        public Builder storageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return this;
        }

        public Builder nodeStore( final NodeStore nodeStore )
        {
            this.nodeStore = nodeStore;
            return this;
        }

        public FindNodesWithVersionDifferenceCommand build()
        {
            return new FindNodesWithVersionDifferenceCommand( this );
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder source( final Branch val )
        {
            source = val;
            return this;
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
        }

        public Builder excludes( final NodeIds nodeIds )
        {
            this.excludes = nodeIds;
            return this;
        }
    }
}
