package com.enonic.wem.core.index.node;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.elastic.ElasticsearchService;
import com.enonic.wem.core.elastic.ElasticsearchQuery;

public abstract class QueryService<Q extends EntityQuery>
{
    private ElasticsearchService elasticsearchService;

    protected abstract ElasticsearchQuery translateQuery( final Q query );

    private QueryResultFactory queryResultFactory = new QueryResultFactory();

    public QueryResult find( final Q query )
    {
        final ElasticsearchQuery elasticsearchQuery = translateQuery( query );

        final SearchResponse searchResponse = elasticsearchService.search( elasticsearchQuery );

        return translateResult( searchResponse );
    }

    private QueryResult translateResult( final SearchResponse searchResponse )
    {
        return queryResultFactory.create( searchResponse );
    }

    @Inject
    public void setElasticsearchService( final ElasticsearchService elasticsearchService )
    {
        this.elasticsearchService = elasticsearchService;
    }

}
