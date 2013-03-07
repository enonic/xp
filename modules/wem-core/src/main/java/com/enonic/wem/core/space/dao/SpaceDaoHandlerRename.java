package com.enonic.wem.core.space.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SpaceAlreadyExistException;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.SpaceName;

final class SpaceDaoHandlerRename
    extends AbstractSpaceDaoHandler
{
    public SpaceDaoHandlerRename( final Session session )
    {
        super( session );
    }

    public boolean handle( final SpaceName spaceName, final String newName )
        throws RepositoryException
    {
        final Node spaceNode = getSpaceNode( spaceName );
        if ( spaceNode == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }

        final String srcPath = spaceNode.getPath();
        final String dstPath = spaceNode.getParent().getPath() + "/" + newName;
        if ( srcPath.equals( dstPath ) )
        {
            return false;
        }

        if ( session.nodeExists( dstPath ) )
        {
            throw new SpaceAlreadyExistException( SpaceName.from( newName ) );
        }

        spaceNode.setProperty( SpaceJcrMapper.NAME, newName );
        session.move( srcPath, dstPath );

        return true;
    }
}
