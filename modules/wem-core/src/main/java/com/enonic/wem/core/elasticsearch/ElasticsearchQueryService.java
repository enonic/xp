package com.enonic.wem.core.elasticsearch;

import javax.inject.Inject;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.index.query.EntityQueryTranslator;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.NodeQueryTranslator;
import com.enonic.wem.core.index.query.QueryResultFactory;
import com.enonic.wem.core.index.query.QueryService;

public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private QueryResultFactory queryResultFactory = new QueryResultFactory();


    @Override
    public NodeQueryResult find( final NodeQuery query, final Workspace workspace )
    {
        return doFind( NodeQueryTranslator.translate( query, workspace ) );
    }

    @Override
    public NodeQueryResult find( final EntityQuery query, final Workspace workspace )
    {
        return doFind( EntityQueryTranslator.translate( query, workspace ) );
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

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
