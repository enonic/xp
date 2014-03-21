package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Nodes;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByIdsService
    extends AbstractNodeService
{
    private final EntityIds entityIds;

    public GetNodesByIdsService( final Session session, final EntityIds entityIds )
    {
        super( session );
        this.entityIds = entityIds;
    }

    public Nodes execute()
    {
        final Nodes.Builder nodes = newNodes();

        for ( final EntityId id : this.entityIds )
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
