package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;

public interface QueryService
{
    public NodeQueryResult find( final NodeQuery query, final IndexContext context );

    public NodeQueryResult find( final EntityQuery query, final IndexContext context );

}
