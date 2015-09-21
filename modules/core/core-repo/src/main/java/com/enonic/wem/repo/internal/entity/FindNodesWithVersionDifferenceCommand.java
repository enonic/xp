package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;

public class FindNodesWithVersionDifferenceCommand
{
    private final NodeVersionDiffQuery query;

    private final SearchService searchService;

    private FindNodesWithVersionDifferenceCommand( Builder builder )
    {
        query = builder.query;
        searchService = builder.searchService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionDiffResult execute()
    {
        return this.searchService.diffNodeVersions( query, InternalContext.from( ContextAccessor.current() ) );
    }

    public static final class Builder
    {
        private NodeVersionDiffQuery query;

        private SearchService searchService;

        private Builder()
        {
        }

        public Builder query( NodeVersionDiffQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder searchService( final SearchService searchService )
        {
            this.searchService = searchService;
            return this;
        }

        public FindNodesWithVersionDifferenceCommand build()
        {
            return new FindNodesWithVersionDifferenceCommand( this );
        }
    }
}