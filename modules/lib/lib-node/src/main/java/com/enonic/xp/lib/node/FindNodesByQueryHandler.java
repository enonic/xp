package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeQueryResultMapper;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;

@SuppressWarnings("unused")
public final class FindNodesByQueryHandler
    extends AbstractFindNodesQueryHandler
{

    private FindNodesByQueryHandler( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
    {
        final NodeQuery nodeQuery = createNodeQuery();
        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return convert( result );
    }

    private NodeQueryResultMapper convert( final FindNodesByQueryResult findQueryResult )
    {
        return new NodeQueryResultMapper( findQueryResult );
    }

    public static final class Builder
        extends AbstractFindNodesQueryHandler.Builder<Builder>
    {
        public FindNodesByQueryHandler build()
        {
            return new FindNodesByQueryHandler( this );
        }
    }
}
