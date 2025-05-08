package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.node.NodeConstants;

class FulltextQueryBuilder
    extends SimpleQueryStringBuilder
{
    public static final String NAME = "fulltext";

    FulltextQueryBuilder( final PropertySet expression )
    {
        super( expression );
    }

    @Override
    public QueryBuilder create()
    {
        final org.elasticsearch.index.query.SimpleQueryStringBuilder builder =
            ( (org.elasticsearch.index.query.SimpleQueryStringBuilder) super.create() ).analyzer(
                NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER ).analyzeWildcard( true );

        fields.getWeightedQueryFieldNames().forEach( field -> {
            final String resolvedName = NAME_RESOLVER.resolve( field.getBaseFieldName(), IndexValueType.ANALYZED );
            if ( field.getWeight() != null )
            {
                builder.field( resolvedName, field.getWeight() );
            }
            else
            {
                builder.field( resolvedName );
            }
        } );

        return builder;
    }

}
