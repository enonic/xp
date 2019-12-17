package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodePath nodePath;

    private final Branch source;

    private final Branch target;

    private final int size;

    private final NodeIds excludes;

    private final NodeSearchService nodeSearchService;

    private final NodeStorageService nodeStorageService;

    private final int batchSize = 10_000;

    private FindNodesWithVersionDifferenceCommand( final Builder builder )
    {
        nodePath = builder.nodePath;
        source = builder.source;
        target = builder.target;
        size = builder.size;
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

        final ExcludeEntries excludeEntries = getExcludePaths( context );

        final SearchResult result = this.nodeSearchService.query( NodeVersionDiffQuery.create().
            source( source ).
            target( target ).
            nodePath( nodePath ).
            excludes( excludeEntries ).
            size( this.size ).
            batchSize( batchSize ).
            build(), SingleRepoStorageSource.create( ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) );

        return NodeVersionDiffResultFactory.create( result );
    }

    private ExcludeEntries getExcludePaths( final InternalContext context )
    {
        if ( this.excludes.isEmpty() )
        {
            return ExcludeEntries.empty();
        }

        final ExcludeEntries.Builder builder = ExcludeEntries.create();

        final NodeBranchEntries result = this.nodeStorageService.getBranchNodeVersions( excludes, false, context );

        for ( final NodeBranchEntry entry : result )
        {
            builder.add( new ExcludeEntry( entry.getNodePath(), false ) );
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

        private int size = NodeSearchService.GET_ALL_SIZE_FLAG;

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

        public Builder size( final int val )
        {
            size = val;
            return this;
        }
    }
}