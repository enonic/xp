package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.api.entity.Node;

public class GetNodeByIdService
    extends NodeService
{
    private final GetNodeById getNodeById;


    public GetNodeByIdService( final Session session, final GetNodeById getNodeByPathCommand )
    {
        super( session );
        this.getNodeById = getNodeByPathCommand;

    }

    public Node execute()
    {
        try
        {
            return nodeJcrDao.getNodeById( getNodeById.getId() );
        }
        catch ( NoEntityWithIdFound ex )
        {
            throw new ContentNotFoundException( getNodeById.getId() );
        }
    }


}
