package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;

import static org.elasticsearch.common.Strings.isNullOrEmpty;

class LikeQueryBuilder
    extends ExpressionQueryBuilder
{
    public static final String NAME = "like";

    private final String value;

    LikeQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.value = getString( "value" );
        Preconditions.checkArgument( !isNullOrEmpty( this.value ), "'Like' value cannot be empty" );
    }

    public QueryBuilder create()
    {
        final String fieldName = getFieldName( value );

        final WildcardQueryBuilder builder =
            QueryBuilders.wildcardQuery( fieldName, (String) parseValue( IndexValueNormalizer.normalize( value ) ) ).queryName( fieldName );

        return addBoost( builder, boost );
    }
}
