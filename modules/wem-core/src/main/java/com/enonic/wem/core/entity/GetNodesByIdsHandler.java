package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByIdsHandler
    extends CommandHandler<GetNodesByIds>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao nodeDao = new NodeJcrDao( session );
        final Nodes.Builder nodes = newNodes();

        for ( final EntityId id : command.getIds() )
        {
            try
            {
                nodes.add( nodeDao.getNodeById( id ) );
            }
            catch ( NoEntityWithIdFound noEntityWithIdFound )
            {
                // Not found
            }
        }
        command.setResult( nodes != null ? nodes.build() : null );
    }
}
