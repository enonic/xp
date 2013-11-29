package com.enonic.wem.core.index.entity;

import com.enonic.wem.query.EntityQuery;

public interface EntitySearchService
{
    public EntitySearchResult find( final EntityQuery entityQuery );
}
