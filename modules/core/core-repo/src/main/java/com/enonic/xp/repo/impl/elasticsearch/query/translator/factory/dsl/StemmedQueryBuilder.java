package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.Locale;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;

class StemmedQueryBuilder
    extends SimpleQueryStringBuilder
{
    public static final String NAME = "stemmed";

    private final Locale language;

    StemmedQueryBuilder( final PropertySet expression )
    {
        super( expression );

        language = Locale.forLanguageTag( getString( "language" ) );
    }

    @Override
    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.SimpleQueryStringBuilder builder =
            ( (org.elasticsearch.index.query.SimpleQueryStringBuilder) super.create() ).analyzeWildcard( true );

        final IndexValueType languageIndexType = IndexLanguageController.resolveStemmedIndexValueType( this.language );
        if ( languageIndexType == null )
        {
            throw new IllegalArgumentException( "Unsupported language for stemmed function: " + language );
        }
        fields.getWeightedQueryFieldNames().forEach( field -> {
            final String resolvedName = NAME_RESOLVER.resolve( field.getBaseFieldName(), languageIndexType );

            if ( field.getWeight() != null )
            {
                builder.field( resolvedName, field.getWeight() );
            }
            else
            {
                builder.field( resolvedName );
            }
        } );

        builder.analyzer( IndexLanguageController.resolveAnalyzer( this.language ) );

        return builder;
    }
}
