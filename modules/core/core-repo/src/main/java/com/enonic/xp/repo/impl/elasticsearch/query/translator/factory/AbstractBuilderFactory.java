package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class AbstractBuilderFactory
{
    protected final QueryFieldNameResolver fieldNameResolver;

    protected AbstractBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        this.fieldNameResolver = fieldNameResolver;
    }


}
