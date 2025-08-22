package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertySet;

import static com.google.common.base.Strings.isNullOrEmpty;

class RangeQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "range";

    private final Object lt;

    private final Object gt;

    private final Object lte;

    private final Object gte;

    RangeQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.lt = getObject( "lt" );
        this.gt = getObject( "gt" );
        this.lte = getObject( "lte" );
        this.gte = getObject( "gte" );

        Preconditions.checkArgument(
            ( gte != null && !isNullOrEmpty( gte.toString() ) ) || ( gt != null && !isNullOrEmpty( gt.toString() ) ) ||
                ( lt != null && !isNullOrEmpty( lt.toString() ) ) || ( lte != null && !isNullOrEmpty( lte.toString() ) ),
            "any of range conditions must be set" );

        Preconditions.checkArgument( ( gte == null || isNullOrEmpty( gte.toString() ) ) || ( gt == null || isNullOrEmpty( gt.toString() ) ),
                                     "'gt' and 'gte' cannot be set both" );
        Preconditions.checkArgument( ( lte == null || isNullOrEmpty( lte.toString() ) ) || ( lt == null || isNullOrEmpty( lt.toString() ) ),
                                     "'gt' and 'gte' cannot be set both" );
    }

    @Override
    public QueryBuilder create()
    {
        final String lowerFieldName = gt != null ? getFieldName( gt ) : gte != null ? getFieldName( gte ) : null;
        final String upperFieldName = lt != null ? getFieldName( lt ) : lte != null ? getFieldName( lte ) : null;

        if ( lowerFieldName != null && upperFieldName != null && !lowerFieldName.equals( upperFieldName ) )
        {
            throw new IllegalArgumentException( "upper and lower conditions must be the same type" );
        }

        final String fieldName = lowerFieldName != null ? lowerFieldName : upperFieldName;

        final var builder = new org.elasticsearch.index.query.RangeQueryBuilder( fieldName ).from( parseValue( gt != null ? gt : gte ) )
            .queryName( fieldName );

        if ( lt != null )
        {
            builder.lt( parseValue( lt ) );
        }

        if ( lte != null )
        {
            builder.lte( parseValue( lte ) );
        }

        if ( gt != null )
        {
            builder.gt( parseValue( gt ) );
        }

        if ( gte != null )
        {
            builder.gte( parseValue( gte ) );
        }

        return addBoost( builder, boost );
    }
}
