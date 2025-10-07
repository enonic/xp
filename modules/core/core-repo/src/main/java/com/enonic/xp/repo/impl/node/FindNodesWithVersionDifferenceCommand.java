package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodePath nodePath;

    private final Branch source;

    private final Branch target;

    private final NodeIds excludes;

    private final NodeSearchService nodeSearchService;

    private final NodeStorageService nodeStorageService;

    private static final int BATCH_SIZE = 20_000;

    private FindNodesWithVersionDifferenceCommand( final Builder builder )
    {
        nodePath = builder.nodePath;
        source = builder.source;
        target = builder.target;
        nodeSearchService = builder.nodeSearchService;
        this.nodeStorageService = builder.nodeStorageService;
        this.excludes = builder.excludes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionDiffResult execute()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        final NodePaths excludeEntries = getExcludePaths( context );

        final SearchResult result = this.nodeSearchService.query( NodeVersionDiffQuery.create()
                                                                      .source( source )
                                                                      .target( target )
                                                                      .nodePath( nodePath )
                                                                      .excludes( excludeEntries )
                                                                      .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                      .batchSize( BATCH_SIZE )
                                                                      .build(), context.getRepositoryId() );

        return NodeVersionDiffResultFactory.create( result );
    }

    private NodePaths getExcludePaths( final InternalContext context )
    {
        if ( this.excludes.isEmpty() )
        {
            return NodePaths.empty();
        }

        final NodePaths.Builder builder = NodePaths.create();

        final NodeBranchEntries result = this.nodeStorageService.getBranchNodeVersions( excludes, context );

        for ( final NodeBranchEntry entry : result )
        {
            builder.addNodePath( entry.getNodePath() );
        }

        return builder.build();
    }

    public static final class Builder
    {
        private NodeSearchService nodeSearchService;

        private NodeStorageService nodeStorageService;

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
