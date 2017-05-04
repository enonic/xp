package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.time.Instant;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class InstantRangeFunctionArg
    extends AbstractRangeFunctionArg<Instant>
{
    @Override
    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( this.fieldName, IndexValueType.DATETIME );
    }
}
