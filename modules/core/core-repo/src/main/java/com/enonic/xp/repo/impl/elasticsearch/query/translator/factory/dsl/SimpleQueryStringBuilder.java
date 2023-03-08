package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.WeightedQueryFieldNames;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static com.google.common.base.Strings.nullToEmpty;

abstract class SimpleQueryStringBuilder
    extends DslQueryBuilder
{
    protected final String query;

    protected final String operator;

    protected final WeightedQueryFieldNames fields;

    protected static final SearchQueryFieldNameResolver NAME_RESOLVER = SearchQueryFieldNameResolver.INSTANCE;

    SimpleQueryStringBuilder( final PropertySet expression )
    {
        super( expression );

        this.query = getString( "query" );
        this.operator = getString( "operator" );
        this.fields = WeightedQueryFieldNames.from( getStrings( "fields" ) );
    }

    @Override
    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.SimpleQueryStringBuilder builder =
            new org.elasticsearch.index.query.SimpleQueryStringBuilder( query ).analyzeWildcard( true );

        if ( !nullToEmpty( operator ).isBlank() )
        {
            builder.defaultOperator( org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.valueOf( operator ) );
        }

        return addBoost( builder, boost );
    }
}
