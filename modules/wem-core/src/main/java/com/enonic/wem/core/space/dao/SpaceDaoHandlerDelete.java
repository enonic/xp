package com.enonic.wem.core.space.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.SpaceName;

class SpaceDaoHandlerDelete
    extends AbstractSpaceDaoHandler
{
    public SpaceDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    public void handle( final SpaceName spaceName )
        throws RepositoryException
    {
        Preconditions.checkArgument( !spaceName.isTemporary(), "Temporary space cannot be deleted" );
        final Node spaceNode = getSpaceNode( spaceName );

        if ( spaceNode == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }

        spaceNode.remove();
    }
}
