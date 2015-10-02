package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;

class NotQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public NotQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    QueryBuilder create( final QueryBuilder query )
    {
        return QueryBuilders.boolQuery().mustNot( query );
    }

}
