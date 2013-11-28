package com.enonic.wem.core.space.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;

public class SpaceDaoHandlerUpdate
    extends AbstractSpaceDaoHandler
{
    public SpaceDaoHandlerUpdate( final Session session )
    {
        super( session );
    }

    public void handle( final Space space )
        throws RepositoryException
    {
        final Node spaceNode = getSpaceNode( space.getName() );
        if ( spaceNode == null )
        {
            throw new SpaceNotFoundException( space.getName() );
        }

        spaceJcrMapper.toJcr( space, spaceNode );
    }
}
