package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class GetNodesByIdsParams
{
    private final EntityIds ids;

    public GetNodesByIdsParams( final EntityIds ids )
    {
        this.ids = ids;
    }

    public void validate()
    {
        Preconditions.checkNotNull( ids, "ids must be specified" );
    }

    public EntityIds getIds()
    {
        return this.ids;
    }
}
