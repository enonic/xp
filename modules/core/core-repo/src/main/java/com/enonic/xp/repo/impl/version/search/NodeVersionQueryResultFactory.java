package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.NodeVersionFactory;

public class NodeVersionQueryResultFactory
{
    public static NodeVersionQueryResult create( final SearchResult searchResult )
    {
        return NodeVersionQueryResult.create()
            .totalHits( searchResult.getTotalHits() )
            .entityVersions( searchResult.getHits()
                                 .stream()
                                 .map( SearchHit::getReturnValues )
                                 .map( NodeVersionFactory::create )
                                 .collect( NodeVersions.collector() ) )
            .build();
    }
}
