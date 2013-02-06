package com.enonic.wem.core.space.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SpaceAlreadyExistException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.jcr.JcrConstants;


final class SpaceDaoHandlerCreate
    extends AbstractSpaceDaoHandler
{
    SpaceDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    void handle( final Space space )
        throws RepositoryException
    {
        final Node root = session.getRootNode();
        final Node spacesNode = root.getNode( SPACES_PATH );
        final SpaceName name = space.getName();

        if ( spacesNode.hasNode( name.name() ) )
        {
            throw new SpaceAlreadyExistException( name );
        }

        final Node spaceNode = spacesNode.addNode( name.name(), JcrConstants.SPACE_TYPE );
        spaceJcrMapper.toJcr( space, spaceNode );
    }

}
