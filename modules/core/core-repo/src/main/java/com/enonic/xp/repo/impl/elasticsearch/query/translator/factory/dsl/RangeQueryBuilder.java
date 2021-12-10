package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertySet;

class RangeQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "range";

    private final Object from;

    private final Object to;

    private final boolean includeFrom;

    private final boolean includeTo;

    RangeQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.includeFrom = getBoolean( "includeFrom" );
        this.includeTo = getBoolean( "includeTo" );
        this.from = getObject( "from" );
        this.to = getObject( "to" );

        Preconditions.checkArgument(
            ( from != null && !Strings.isNullOrEmpty( from.toString() ) ) || ( to != null && !Strings.isNullOrEmpty( to.toString() ) ),
            "Either 'from' or 'to' must be set and not empty" );
    }

    public QueryBuilder create()
    {
        final String fieldName = getFieldName( from != null ? from : to );

        final var builder = new org.elasticsearch.index.query.RangeQueryBuilder( fieldName ).from( parseValue( from ) )
            .to( parseValue( to ) )
            .includeLower( includeFrom )
            .includeUpper( includeTo )
            .queryName( fieldName );

        return addBoost( builder );
    }
}
