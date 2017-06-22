package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class NumericRangeFunctionArg
    extends AbstractRangeFunctionArg<Double>
{
    @Override
    public String getFieldName()
    {
        return new SearchQueryFieldNameResolver().resolve( this.fieldName, IndexValueType.NUMBER );
    }
}
