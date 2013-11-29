package com.enonic.wem.core.index.query;

import com.enonic.wem.core.index.elastic.ElasticsearchQuery;
import com.enonic.wem.query.EntityQuery;

public class EntityQueryTranslator
{
    private ElasticsearchQueryBuilderFactory queryBuilderFactory = new ElasticsearchQueryBuilderFactory();

    private ElasticsearchFilterBuilderFactory filterBuilderFactory = new ElasticsearchFilterBuilderFactory();

    public ElasticsearchQuery translate( final EntityQuery entityQuery )
    {
        ElasticsearchQuery elasticsearchQuery = new ElasticsearchQuery();

        elasticsearchQuery.setQuery( queryBuilderFactory.create( entityQuery.getQuery() ) );

        elasticsearchQuery.setFilter( filterBuilderFactory.create( entityQuery.getQueryFilters() ) );

        return elasticsearchQuery;
    }


}
