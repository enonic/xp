package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.exception.SpaceNotFoundException;

public class DeleteSpaceDaoHandler
    extends AbstractSpaceDaoHandler
{
    public DeleteSpaceDaoHandler( final Session session )
    {
        super( session );
    }

    public void handle( final SpaceName spaceName )
        throws RepositoryException
    {
        final Node spaceNode = getSpaceNode( spaceName );

        if ( spaceNode == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }

        spaceNode.remove();
    }
}
