package com.enonic.wem.core.index.query;

import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;

public interface QueryService
{
    public NodeQueryResult find( final NodeQuery query, final IndexContext context );

}
