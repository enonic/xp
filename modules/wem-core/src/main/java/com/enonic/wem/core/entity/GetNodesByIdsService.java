package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Nodes;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByIdsService
    extends NodeService
{

    private final GetNodesByIds command;


    public GetNodesByIdsService( final Session session, final GetNodesByIds command )
    {
        super( session );
        this.command = command;
    }

    public Nodes execute()
    {
        final Nodes.Builder nodes = newNodes();

        for ( final EntityId id : command.getIds() )
        {
            try
            {
                nodes.add( nodeJcrDao.getNodeById( id ) );
            }
            catch ( NoEntityWithIdFoundException noEntityWithIdFoundException )
            {
                throw new NoEntityWithIdFoundException( id );
            }
        }

        return nodes.build();
    }

}
