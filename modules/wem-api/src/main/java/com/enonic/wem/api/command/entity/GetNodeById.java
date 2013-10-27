package com.enonic.wem.api.command.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;

public class GetNodeById
    extends Command<Node>
{
    private final EntityId id;

    public GetNodeById( final EntityId id )
    {
        this.id = id;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( id, "id must be specified" );
    }

    public EntityId getId()
    {
        return this.id;
    }
}
