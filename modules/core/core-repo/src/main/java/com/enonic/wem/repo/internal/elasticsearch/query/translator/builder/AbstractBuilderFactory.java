package com.enonic.wem.repo.internal.elasticsearch.query.translator.builder;

import com.enonic.wem.repo.internal.elasticsearch.query.translator.QueryFieldNameResolver;

public class AbstractBuilderFactory
{
    protected final QueryFieldNameResolver fieldNameResolver;

    protected AbstractBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        this.fieldNameResolver = fieldNameResolver;
    }


}
