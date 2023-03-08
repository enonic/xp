package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.PropertySet;

class MatchAllQueryBuilder
    extends DslQueryBuilder
{
    public static final String NAME = "matchAll";

    MatchAllQueryBuilder( final PropertySet expression )
    {
        super( expression );
    }

    @Override
    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        return addBoost( query, boost );
    }
}
