package com.enonic.wem.core.elasticsearch;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.elasticsearch.query.EntityQueryTranslator;
import com.enonic.wem.core.elasticsearch.query.NodeQueryTranslator;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.QueryResultFactory;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.index.result.SearchResult;

public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private QueryResultFactory queryResultFactory = new QueryResultFactory();


    @Override
    public NodeQueryResult find( final NodeQuery query, final IndexContext context )
    {
        return doFind( NodeQueryTranslator.translate( query, context ) );
    }

    @Override
    public NodeQueryResult find( final EntityQuery query, final IndexContext context )
    {
        return doFind( EntityQueryTranslator.translate( query, context ) );
    }

    private NodeQueryResult doFind( final ElasticsearchQuery query )
    {
        final SearchResult searchResult = elasticsearchDao.search( query );

        return translateResult( searchResult );
    }

    private NodeQueryResult translateResult( final SearchResult searchResult )
    {
        return queryResultFactory.create( searchResult );
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
