package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.common.base.Strings;

import com.enonic.xp.data.PropertySet;

class ExistsQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "exists";

    ExistsQueryBuilder( final PropertySet expression )
    {
        super( expression );
    }

    @Override
    public QueryBuilder create()
    {
        final String fieldName = getFieldName( null );

        if ( Strings.nullToEmpty( fieldName ).isBlank() )
        {
            throw new IllegalArgumentException( "'field' cannot be empty" );
        }

        return QueryBuilders.existsQuery( fieldName ).queryName( fieldName );
    }
}
