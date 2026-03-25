package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.time.Instant;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

public class InstantRangeFunctionArg
    extends AbstractRangeFunctionArg<Instant>
{
    @Override
    public String getFieldName()
    {
        return SearchQueryFieldNameResolver.INSTANCE.resolve( this.fieldName, StaticIndexValueType.DATETIME );
    }
}
