package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.elasticsearch.OrderbyValueResolver;

class OrderByTypeFactory
{
    static IndexItem create( final IndexPath indexPath, final Value propertyValue )
    {
        return new IndexItemOrderBy( indexPath, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

}
