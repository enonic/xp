package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.PropertySet;

class MatchQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "match";

    private final Object value;

    MatchQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.value = getObject( "value" );
    }

    public QueryBuilder create()
    {
        final String fieldName = getFieldName( value );

        final var builder = QueryBuilders.matchQuery( fieldName, parseValue( value ) ).queryName( fieldName );

        return addBoost( builder, boost );
    }
}
