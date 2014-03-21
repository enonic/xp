package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByPathsService
    extends AbstractNodeService
{
    private final NodePaths nodePaths;

    private boolean failWithExceptionAtNoNodeFound = true;

    public GetNodesByPathsService( final Session session, final NodePaths nodePaths )
    {
        super( session );
        this.nodePaths = nodePaths;
    }

    public GetNodesByPathsService failWithExceptionAtNoNodeFound( final boolean value )
    {
        this.failWithExceptionAtNoNodeFound = value;
        return this;
    }

    public Nodes execute()
    {
        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : nodePaths )
        {
            try
            {
                nodes.add( nodeJcrDao.getNodeByPath( path ) );
            }
            catch ( NoNodeAtPathFoundException noNodeAtPathFoundException )
            {
                if ( failWithExceptionAtNoNodeFound )
                {
                    throw new NoNodeAtPathFoundException( path );
                }
            }
        }

        return nodes.build();
    }

}
