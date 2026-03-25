package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

public class NumericRangeFunctionArg
    extends AbstractRangeFunctionArg<Double>
{
    @Override
    public String getFieldName()
    {
        return SearchQueryFieldNameResolver.INSTANCE.resolve( this.fieldName, StaticIndexValueType.NUMBER );
    }
}
