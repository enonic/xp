package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeHasChildResolver
{
    private final NodeSearchService nodeSearchService;

    private NodeHasChildResolver( Builder builder )
    {
        this.nodeSearchService = builder.nodeSearchService;
    }

    public boolean resolve( final NodePath nodePath )
    {
        return doResolve( nodePath );
    }

    private boolean doResolve( final NodePath nodePath )
    {
        final SearchResult result = this.nodeSearchService.query( NodeQuery.create().
            parent( nodePath ).
            searchMode( SearchMode.COUNT ).
            build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        return result.getTotalHits() > 0;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeSearchService nodeSearchService;

        private Builder()
        {
        }

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
