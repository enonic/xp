package com.enonic.xp.query;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.suggester.SuggestionQueries;

public interface Query
{
    ImmutableList<OrderExpr> getOrderBys();

    QueryExpr getQuery();

    Filters getPostFilters();

    Filters getQueryFilters();

    AggregationQueries getAggregationQueries();

    SuggestionQueries getSuggestionQueries();

    int getFrom();

    int getSize();

    SearchMode getSearchMode();

    boolean isExplain();

}
