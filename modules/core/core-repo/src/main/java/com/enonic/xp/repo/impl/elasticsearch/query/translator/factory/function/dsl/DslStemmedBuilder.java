package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl;

import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.dsl.DslSimpleStringQueryBuilder;
import com.enonic.xp.repo.impl.index.IndexStemmedController;

public class DslStemmedBuilder
    extends DslSimpleStringQueryBuilder
{
    private final String language;

    public DslStemmedBuilder( final PropertySet expression )
    {
        super( expression );

        language = getString( "language" );
    }

    public QueryBuilder create()
    {
        final SimpleQueryStringBuilder builder = ( (SimpleQueryStringBuilder) super.create() )
            .analyzeWildcard( true );

        fields.getWeightedQueryFieldNames().forEach( field ->
                 Optional.ofNullable(
                     IndexStemmedController.resolveIndexValueType( this.language ) )
                         .map( type -> nameResolver.resolve( field.getBaseFieldName(), type ) )
                         .ifPresent( fieldName -> builder.field( fieldName, field.getWeight() ) ) );

        if ( language != null )
        {
            builder.analyzer( IndexStemmedController.resolveAnalyzer( language ) );
        }

        return builder;
    }
}
