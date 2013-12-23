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

    private boolean failWithExceptionAtNoNodeFound = true;

    public GetNodesByPathsService( final Session session, final GetNodesByPaths command )
    {
        super( session );
        this.command = command;
    }

    public GetNodesByPathsService failWithExceptionAtNoNodeFound( final boolean value )
    {
        this.failWithExceptionAtNoNodeFound = value;
        return this;
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
                if ( failWithExceptionAtNoNodeFound )
                {
                    throw new NoNodeAtPathFound( path );
                }
            }
        }

        return nodes.build();
    }

}
