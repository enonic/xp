package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.GetNodesByPathsParams;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByPathsCommand
{
    private GetNodesByPathsParams params;

    private Session session;

    private boolean failWithExceptionAtNoNodeFound = true;

    public Nodes execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Nodes doExecute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : params.getPaths() )
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

    public GetNodesByPathsCommand failWithExceptionAtNoNodeFound( final boolean value )
    {
        this.failWithExceptionAtNoNodeFound = value;
        return this;
    }

    public GetNodesByPathsCommand params( final GetNodesByPathsParams params )
    {
        this.params = params;
        return this;
    }

    public GetNodesByPathsCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
