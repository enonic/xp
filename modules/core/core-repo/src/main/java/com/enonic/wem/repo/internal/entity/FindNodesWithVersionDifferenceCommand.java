package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.wem.repo.internal.version.NodeVersionDiffQuery;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
import com.enonic.xp.node.NodeVersionDiffResult;

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