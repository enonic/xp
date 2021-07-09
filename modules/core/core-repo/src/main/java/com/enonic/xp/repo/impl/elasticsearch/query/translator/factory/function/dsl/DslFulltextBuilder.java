package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.dsl;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.dsl.DslSimpleStringQueryBuilder;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.node.NodeConstants;

public class DslFulltextBuilder
    extends DslSimpleStringQueryBuilder
{
    public DslFulltextBuilder( final PropertySet expression )
    {
        super( expression );
    }

    public QueryBuilder create()
    {
        if ( query == null || query.isEmpty() )
        {
            return new MatchAllQueryBuilder();
        }

        final SimpleQueryStringBuilder builder = ( (SimpleQueryStringBuilder) super.create() )
            .analyzer( NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER );

            fields.forEach( field -> builder.field( nameResolver.resolve( field.getBaseFieldName(), IndexValueType.ANALYZED ), field.getWeight()) );

        return builder;
    }

}
