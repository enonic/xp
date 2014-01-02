package com.enonic.wem.core.index.entity;

import com.enonic.wem.api.entity.EntityQuery;

public interface EntitySearchService
{
    public EntityQueryResult find( final EntityQuery entityQuery );
}
