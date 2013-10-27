package com.enonic.wem.api.command.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByIds
    extends Command<Nodes>
{
    private final EntityIds ids;

    public GetNodesByIds( final EntityIds ids )
    {
        this.ids = ids;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( ids, "ids must be specified" );
    }

    public EntityIds getIds()
    {
        return this.ids;
    }
}


