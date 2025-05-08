package com.enonic.xp.query;

import java.util.List;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.suggester.SuggestionQueries;

public interface Query
{
    List<OrderExpr> getOrderBys();

    QueryExpr getQuery();

    Filters getPostFilters();

    Filters getQueryFilters();

    AggregationQueries getAggregationQueries();

    SuggestionQueries getSuggestionQueries();

    HighlightQuery getHighlight();

    int getFrom();

    int getSize();

    SearchMode getSearchMode();

    boolean isExplain();

}
