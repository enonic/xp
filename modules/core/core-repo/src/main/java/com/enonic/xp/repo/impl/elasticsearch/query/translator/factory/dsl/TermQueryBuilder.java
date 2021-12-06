package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.PropertySet;

class TermQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "term";

    private final Object value;

    private final Double boost;

    TermQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.value = getObject( "value" );
        this.boost = getDouble( "boost" );
    }

    public QueryBuilder create()
    {
        final String fieldName = getFieldName( value );

        final org.elasticsearch.index.query.TermQueryBuilder builder =
            QueryBuilders.termQuery( fieldName, parseValue( value ) ).queryName( fieldName );

        if ( boost != null )
        {
            builder.boost( boost.floatValue() );
        }
        return builder;
    }
}
