package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertySet;

class TermQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "term";

    private final Object value;

    TermQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.value = getObject( "value" );
    }

    public QueryBuilder create()
    {
        Preconditions.checkArgument( !"geoPoint".equals( type ), "term query doesn't support [geoPoint] type" );

        final String fieldName = getFieldName( value );

        final var builder = QueryBuilders.termQuery( fieldName, parseValue( value ) ).queryName( fieldName );

        return addBoost( builder );
    }
}
