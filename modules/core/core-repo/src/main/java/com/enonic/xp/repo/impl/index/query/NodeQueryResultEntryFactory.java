package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.repo.impl.search.result.SearchHit;

class NodeQueryResultEntryFactory
{
    public static NodeQueryResultEntry create( final SearchHit searchHit )
    {
        return new NodeQueryResultEntry( searchHit.getScore(), searchHit.getId(), searchHit.getReturnValues() );
    }
}
