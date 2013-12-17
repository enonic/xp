package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByPathsService
    extends NodeService
{

    private final GetNodesByPaths command;

    public GetNodesByPathsService( final Session session, final GetNodesByPaths command )
    {
        super( session );
        this.command = command;
    }

    public Nodes execute()
    {
        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : command.getPaths() )
        {
            try
            {
                nodes.add( nodeJcrDao.getNodeByPath( path ) );
            }
            catch ( NoNodeAtPathFound noNodeAtPathFound )
            {
                // Node not found, just continue for now
            }
        }

        return nodes.build();
    }

}
