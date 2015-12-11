package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.data.Value;
import com.enonic.xp.repo.impl.elasticsearch.OrderbyValueResolver;

public class OrderByTypeFactory
{
    static IndexItem create( final String path, final Value propertyValue )
    {
        return new IndexItemOrderBy( path, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

}
