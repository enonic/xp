package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByParentService
    extends NodeService
{
    private final GetNodesByParent command;

    public GetNodesByParentService( final Session session, final GetNodesByParent command )
    {
        super( session );
        this.command = command;
    }

    public Nodes execute()
    {
        return nodeJcrDao.getNodesByParentPath( command.getParent() );
    }

}
