package com.enonic.wem.core.index.elastic.searchsource;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;


public class SearchSource
{

    private FilterBuilder filterBuilder;

    private QueryBuilder queryBuilder;


    public FilterBuilder getFilterBuilder()
    {
        return filterBuilder;
    }

    public void setFilterBuilder( final FilterBuilder filterBuilder )
    {
        this.filterBuilder = filterBuilder;
    }

    public QueryBuilder getQueryBuilder()
    {
        return queryBuilder;
    }

    public void setQueryBuilder( final QueryBuilder queryBuilder )
    {
        this.queryBuilder = queryBuilder;
    }
}
