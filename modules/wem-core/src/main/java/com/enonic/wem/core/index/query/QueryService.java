package com.enonic.wem.core.index.query;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.elastic.ElasticsearchDao;
import com.enonic.wem.core.elastic.ElasticsearchQuery;

public abstract class QueryService<Q extends EntityQuery>
{
    private ElasticsearchDao elasticsearchDao;

    protected abstract ElasticsearchQuery translateQuery( final Q query );

    private QueryResultFactory queryResultFactory = new QueryResultFactory();

    public QueryResult find( final Q query )
    {
        final ElasticsearchQuery elasticsearchQuery = translateQuery( query );

        final SearchResponse searchResponse = elasticsearchDao.search( elasticsearchQuery );

        return translateResult( searchResponse );
    }

    private QueryResult translateResult( final SearchResponse searchResponse )
    {
        return queryResultFactory.create( searchResponse );
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

}
