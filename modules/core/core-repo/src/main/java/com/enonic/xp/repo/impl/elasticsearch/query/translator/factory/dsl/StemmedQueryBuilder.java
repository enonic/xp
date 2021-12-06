package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.index.IndexStemmedController;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

class StemmedQueryBuilder
    extends SimpleStringQueryBuilder
{
    public static final String NAME = "stemmed";

    private final String language;

    StemmedQueryBuilder( final PropertySet expression )
    {
        super( expression );

        language = getString( "language" );
    }

    public QueryBuilder create()
    {
        final SimpleQueryStringBuilder builder = ( (SimpleQueryStringBuilder) super.create() ).analyzeWildcard( true );

        final IndexValueTypeInterface languageIndexType =
            Optional.ofNullable( IndexStemmedController.resolveIndexValueType( this.language ) )
                .orElseThrow( () -> new IllegalArgumentException( "Invalid language: " + language ) );

        fields.getWeightedQueryFieldNames().forEach( field -> {
            final String resolvedName = nameResolver.resolve( field.getBaseFieldName(), languageIndexType );

            if ( field.getWeight() != null )
            {
                builder.field( resolvedName, field.getWeight() );
            }
            else
            {
                builder.field( resolvedName );
            }
        } );

        builder.analyzer( IndexStemmedController.resolveAnalyzer( this.language ) );

        return builder;
    }
}
