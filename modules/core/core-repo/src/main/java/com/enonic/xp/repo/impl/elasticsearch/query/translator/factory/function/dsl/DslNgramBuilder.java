package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.dsl.DslSimpleStringQueryBuilder;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.node.NodeConstants;

public class DslNgramBuilder
    extends DslSimpleStringQueryBuilder
{
    public DslNgramBuilder( final PropertySet expression )
    {
        super( expression );
    }

    public QueryBuilder create()
    {
        final SimpleQueryStringBuilder builder = ( (SimpleQueryStringBuilder) super.create() )
            .analyzer( NodeConstants.DEFAULT_NGRAM_SEARCH_ANALYZER );

        fields.forEach(
            field -> builder.field( nameResolver.resolve( field.getBaseFieldName(), IndexValueType.NGRAM ), field.getWeight() ) );

        return builder;

    }
}
