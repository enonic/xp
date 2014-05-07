package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.api.entity.query.NodeQuery;

public interface QueryService
{
    public QueryResult find( final NodeQuery query );

    public QueryResult find( final EntityQuery query );

}
