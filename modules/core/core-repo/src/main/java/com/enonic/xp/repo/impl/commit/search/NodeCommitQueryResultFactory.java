package com.enonic.xp.repo.impl.commit.search;

import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.repo.impl.commit.storage.NodeCommitEntryFactory;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeCommitQueryResultFactory
{
    public static NodeCommitQueryResult create( final SearchResult searchResult )
    {
        return NodeCommitQueryResult.create()
            .totalHits( searchResult.getTotalHits() )
            .nodeCommitEntries( searchResult.getHits()
                                    .stream()
                                    .map( SearchHit::getReturnValues )
                                    .map( NodeCommitEntryFactory::create )
                                    .collect( NodeCommitEntries.collector() ) )
            .build();
    }
}
