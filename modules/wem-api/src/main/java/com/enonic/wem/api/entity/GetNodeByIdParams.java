package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class GetNodeByIdParams
{
    private final EntityId id;

    public GetNodeByIdParams( final EntityId id )
    {
        this.id = id;
    }

    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be specified" );
    }

    public EntityId getId()
    {
        return this.id;
    }
}
