package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMultiRepoQueryResultMapper;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchTargets;

class FindNodesByMultiNodeQueryHandler
    extends AbstractFindNodesQueryHandler
{
    private final SearchTargets searchTargets;

    private FindNodesByMultiNodeQueryHandler( final Builder builder )
    {
        super( builder );
        searchTargets = builder.searchTargets;
    }

    @Override
    public Object execute()
    {
        final NodeQuery nodeQuery = createNodeQuery();

        final MultiRepoNodeQuery multiRepoNodeQuery = new MultiRepoNodeQuery( this.searchTargets, nodeQuery );

        final FindNodesByMultiRepoQueryResult result = this.nodeService.findByQuery( multiRepoNodeQuery );

        return convert( result );
    }

    private NodeMultiRepoQueryResultMapper convert( final FindNodesByMultiRepoQueryResult result )
    {
        return new NodeMultiRepoQueryResultMapper( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractFindNodesQueryHandler.Builder<Builder>
    {
        private SearchTargets searchTargets;

        private Builder()
        {
            super();
        }

        public Builder searchTargets( final SearchTargets val )
        {
            searchTargets = val;
            return this;
        }

        public FindNodesByMultiNodeQueryHandler build()
        {
            return new FindNodesByMultiNodeQueryHandler( this );
        }
    }
}
