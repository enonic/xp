package com.enonic.wem.core.elasticsearch;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.index.query.EntityQueryTranslator;
import com.enonic.wem.core.index.query.QueryResult;
import com.enonic.wem.core.index.query.QueryResultFactory;
import com.enonic.wem.core.index.query.QueryService;

public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private QueryResultFactory queryResultFactory = new QueryResultFactory();

    private EntityQueryTranslator translator = new EntityQueryTranslator();

    @Override
    public QueryResult find( final NodeQuery query )
    {
        return doFind( translator.translate( query ) );
    }

    @Override
    public QueryResult find( final EntityQuery query )
    {
        return doFind( translator.translate( query ) );
    }

    private QueryResult doFind( final ElasticsearchQuery query )
    {
        final SearchResult searchResult = elasticsearchDao.search( query );

        return translateResult( searchResult );
    }

    private QueryResult translateResult( final SearchResult searchResult )
    {
        return queryResultFactory.create( searchResult );
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
