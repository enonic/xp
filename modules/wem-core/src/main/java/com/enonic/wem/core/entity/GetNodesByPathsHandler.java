package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByPathsHandler
    extends CommandHandler<GetNodesByPaths>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao nodeDao = new NodeJcrDao( session );
        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : command.getPaths() )
        {
            try
            {
                nodes.add( nodeDao.getNodeByPath( path ) );
            }
            catch ( NoNodeAtPathFound noNodeAtPathFound )
            {
                // Not found
            }
        }
        command.setResult( nodes.build() );
    }
}
