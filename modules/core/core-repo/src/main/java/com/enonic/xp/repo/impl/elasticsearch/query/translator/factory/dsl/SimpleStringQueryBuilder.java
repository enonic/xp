package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.WeightedQueryFieldNames;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

abstract class SimpleStringQueryBuilder
    extends DslQueryBuilder
{
    protected final String query;

    protected final String operator;

    protected final WeightedQueryFieldNames fields;

    protected final SearchQueryFieldNameResolver nameResolver;

    SimpleStringQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.query = getString( "query" );
        this.operator = getString( "operator" );
        final String field = getString( "field" );
        final List<String> fields = getStrings( "fields" );

        if ( field != null )
        {
            this.fields = WeightedQueryFieldNames.from( field );
        }
        else
        {
            this.fields = WeightedQueryFieldNames.from( fields );
        }

        nameResolver = new SearchQueryFieldNameResolver();
    }

    public QueryBuilder create()
    {
        final SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder( query ).analyzeWildcard( true );

        if ( operator != null && !operator.isEmpty() )
        {
            builder.defaultOperator( SimpleQueryStringBuilder.Operator.valueOf( operator ) );
        }

        return addBoost( builder, boost );
    }
}
