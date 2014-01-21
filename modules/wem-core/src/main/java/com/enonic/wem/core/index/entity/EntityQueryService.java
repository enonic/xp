package com.enonic.wem.core.index.entity;

import com.enonic.wem.api.entity.query.EntityQuery;

public interface EntityQueryService
{
    public EntityQueryResult find( final EntityQuery entityQuery );
}
