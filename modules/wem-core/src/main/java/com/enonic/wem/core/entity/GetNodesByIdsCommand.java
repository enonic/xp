package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

final class GetNodesByIdsCommand
{
    private EntityIds entityIds;

    private Session session;

    Nodes execute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

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

    GetNodesByIdsCommand entityIds( final EntityIds entityIds )
    {
        this.entityIds = entityIds;
        return this;
    }

    GetNodesByIdsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
