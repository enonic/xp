package com.enonic.xp.repo.impl.entity;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class FindNodesWithVersionDifferenceCommand
{
    private final FindNodesWithVersionDifferenceParams params;

    private final SearchService searchService;

    private FindNodesWithVersionDifferenceCommand( Builder builder )
    {
        params = builder.query;
        searchService = builder.searchService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionDiffResult execute()
    {
        return this.searchService.search( NodeVersionDiffQuery.create().
            source( params.getSource() ).
            target( params.getTarget() ).
            nodePath( params.getNodePath() ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            build(), InternalContext.from( ContextAccessor.current() ) );
    }

    public static final class Builder
    {
        private FindNodesWithVersionDifferenceParams query;

        private SearchService searchService;

        private Builder()
        {
        }

        public Builder query( FindNodesWithVersionDifferenceParams query )
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