package com.enonic.wem.repo.internal.elasticsearch.query;

import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.Query;

public class ElasticsearchQueryTranslator
{
    public static ElasticsearchQuery translate( final SearchRequest searchRequest )
    {
        final Query query = searchRequest.getQuery();

        if ( query instanceof NodeQuery )
        {
            return NodeQueryTranslator.translate( searchRequest );
        }

        if ( query instanceof NodeVersionQuery )
        {
            return NodeVersionQueryTranslator.translate( searchRequest );
        }

        throw new UnsupportedOperationException( "Queries of type " + query.getClass() + " not implemented yes" );
    }

}
