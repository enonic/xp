package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.entity.Node;

public class GetNodeByIdService
    extends NodeService
{
    private final GetNodeById getNodeById;

    public GetNodeByIdService( final Session session, final GetNodeById command )
    {
        super( session );
        this.getNodeById = command;
    }

    public Node execute()
    {
        return nodeJcrDao.getNodeById( getNodeById.getId() );
    }
}
