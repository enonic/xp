package com.enonic.wem.api.command.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityId;

public class DeleteNodeById
    extends Command<DeleteNodeResult>
{
    private final EntityId id;

    public DeleteNodeById( final EntityId id )
    {
        this.id = id;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be set" );
    }

    public EntityId getId()
    {
        return id;
    }
}
