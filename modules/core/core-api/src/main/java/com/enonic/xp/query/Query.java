package com.enonic.xp.query;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;

public interface Query
{
    public ImmutableList<OrderExpr> getOrderBys();

    public QueryExpr getQuery();

    public Filters getPostFilters();

    public Filters getQueryFilters();

    public AggregationQueries getAggregationQueries();

    public int getFrom();

    public int getSize();

    public SearchMode getSearchMode();

}
