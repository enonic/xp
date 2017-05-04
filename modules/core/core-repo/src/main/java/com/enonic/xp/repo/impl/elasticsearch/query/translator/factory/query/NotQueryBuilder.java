package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

class NotQueryBuilder
{
    public static QueryBuilder build( final QueryBuilder query )
    {
        return QueryBuilders.boolQuery().mustNot( query );
    }

}
