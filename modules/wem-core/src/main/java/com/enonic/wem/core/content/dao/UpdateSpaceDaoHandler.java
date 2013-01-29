package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.exception.SpaceNotFoundException;

public class UpdateSpaceDaoHandler
    extends AbstractSpaceDaoHandler
{
    public UpdateSpaceDaoHandler( final Session session )
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
