package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.PropertySet;

class InQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "in";

    private final List<Object> values;

    InQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.values = getObjects( "values" );
    }

    public QueryBuilder create()
    {
        if ( values.isEmpty() )
        {
            throw new IllegalArgumentException( "Cannot build empty 'IN' statements" );
        }

        final BoolQueryBuilder query = QueryBuilders.boolQuery();

        for ( final Object value : values )
        {
            final String fieldName = getFieldName( value );
            query.should( QueryBuilders.termQuery( fieldName, parseValue( value ) ).queryName( fieldName ) );
        }

        return addBoost( query, boost );
    }
}
