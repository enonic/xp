package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

final class GetNodesByPathsCommand
{
    private NodePaths nodePaths;

    private Session session;

    private boolean failWithExceptionAtNoNodeFound = true;

    Nodes execute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : this.nodePaths )
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

    GetNodesByPathsCommand failWithExceptionAtNoNodeFound( final boolean value )
    {
        this.failWithExceptionAtNoNodeFound = value;
        return this;
    }

    GetNodesByPathsCommand nodePaths( final NodePaths nodePaths )
    {
        this.nodePaths = nodePaths;
        return this;
    }

    GetNodesByPathsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
