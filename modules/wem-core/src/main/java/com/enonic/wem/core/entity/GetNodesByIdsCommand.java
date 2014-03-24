package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetNodesByIdsParams;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByIdsCommand
{
    private GetNodesByIdsParams params;

    private Session session;

    public Nodes execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Nodes doExecute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final Nodes.Builder nodes = newNodes();

        for ( final EntityId id : params.getIds() )
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

    public GetNodesByIdsCommand params( final GetNodesByIdsParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodesByIdsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
