package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

public class StringRangeFunctionArg
    extends AbstractRangeFunctionArg<String>
{
    @Override
    public String getFieldName()
    {
        return SearchQueryFieldNameResolver.INSTANCE.resolve( this.fieldName, StaticIndexValueType.STRING );
    }

}
