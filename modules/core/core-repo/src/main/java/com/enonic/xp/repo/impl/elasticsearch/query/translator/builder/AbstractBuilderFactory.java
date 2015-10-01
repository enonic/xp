package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;

public class AbstractBuilderFactory
{
    protected final QueryFieldNameResolver fieldNameResolver;

    protected AbstractBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        this.fieldNameResolver = fieldNameResolver;
    }


}
