package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.WeightedQueryFieldNames;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static com.google.common.base.Strings.nullToEmpty;

abstract class SimpleQueryStringBuilder
    extends DslQueryBuilder
{
    protected final String searchString;

    protected final String operator;

    protected final WeightedQueryFieldNames fields;

    protected final SearchQueryFieldNameResolver nameResolver;

    SimpleQueryStringBuilder( final PropertySet expression )
    {
        super( expression );

        this.searchString = getString( "searchString" );
        this.operator = getString( "operator" );
        this.fields = WeightedQueryFieldNames.from( getStrings( "fields" ) );

        nameResolver = new SearchQueryFieldNameResolver();
    }

    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.SimpleQueryStringBuilder builder =
            new org.elasticsearch.index.query.SimpleQueryStringBuilder( searchString ).analyzeWildcard( true );

        if ( !nullToEmpty( operator ).isBlank() )
        {
            builder.defaultOperator( org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.valueOf( operator ) );
        }

        return addBoost( builder, boost );
    }
}
