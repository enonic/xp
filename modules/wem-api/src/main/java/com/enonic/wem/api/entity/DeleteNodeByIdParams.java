package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class DeleteNodeByIdParams
{
    private final EntityId id;

    public DeleteNodeByIdParams( final EntityId id )
    {
        this.id = id;
    }

    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be set" );
    }

    public EntityId getId()
    {
        return id;
    }
}
