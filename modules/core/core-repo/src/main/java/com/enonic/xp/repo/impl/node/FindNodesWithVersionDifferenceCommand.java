package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.storage.StorageService;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Branch source;

    private final Branch target;

    private final OrderExpr orderExpr;

    private final int size;

    private final int from;

    private final NodeIds excludes;

    private final SearchService searchService;

    private final StorageService storageService;

    private final int batchSize = 5_000;

    private FindNodesWithVersionDifferenceCommand( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        source = builder.source;
        target = builder.target;
        orderExpr = builder.orderExpr;
        size = builder.size;
        from = builder.from;
        searchService = builder.searchService;
        this.storageService = builder.storageService;
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

        return this.searchService.query( NodeVersionDiffQuery.create().
            source( source ).
            target( target ).
            nodePath( nodePath ).
            excludes( excludeEntries ).
            size( this.size ).
            batchSize( batchSize ).
            build(), context );
    }

    private ExcludeEntries getExcludePaths( final InternalContext context )
    {
        if ( this.excludes.isEmpty() )
        {
            return ExcludeEntries.empty();
        }

        final ExcludeEntries.Builder builder = ExcludeEntries.create();

        final NodeBranchEntries result = this.storageService.getBranchNodeVersions( excludes, context );

        for ( final NodeBranchEntry entry : result )
        {
            builder.add( new ExcludeEntry( entry.getNodePath(), false ) );
        }

        return builder.build();
    }

    public static final class Builder
    {
        private SearchService searchService;

        private StorageService storageService;

        private NodeId nodeId;

        private NodePath nodePath;

        private Branch source;

        private Branch target;

        private OrderExpr orderExpr;

        private NodeIds excludes = NodeIds.empty();

        private int size = SearchService.GET_ALL_SIZE_FLAG;

        private int from;

        private Builder()
        {
        }


        public Builder searchService( final SearchService searchService )
        {
            this.searchService = searchService;
            return this;
        }

        public Builder storageService( final StorageService storageService )
        {
            this.storageService = storageService;
            return this;
        }

        public FindNodesWithVersionDifferenceCommand build()
        {
            return new FindNodesWithVersionDifferenceCommand( this );
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
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

        public Builder orderExpr( final OrderExpr val )
        {
            orderExpr = val;
            return this;
        }

        public Builder size( final int val )
        {
            size = val;
            return this;
        }

        public Builder from( final int val )
        {
            from = val;
            return this;
        }
    }
}