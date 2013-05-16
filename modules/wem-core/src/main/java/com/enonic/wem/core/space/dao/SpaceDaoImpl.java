package com.enonic.wem.core.space.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;


public final class SpaceDaoImpl
    implements SpaceDao
{

    @Override
    public void createSpace( final Space space, final Session session )
    {
        try
        {
            new SpaceDaoHandlerCreate( session ).handle( space );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Space getSpace( final SpaceName spaceName, final Session session )
    {
        try
        {
            return new SpaceDaoHandlerGet( session ).getSpace( spaceName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Spaces getAllSpaces( final Session session )
    {
        try
        {
            return new SpaceDaoHandlerGet( session ).getAllSpaces();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void updateSpace( final Space space, final Session session )
    {
        try
        {
            new SpaceDaoHandlerUpdate( session ).handle( space );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteSpace( final SpaceName spaceName, final Session session )
    {
        try
        {
            new SpaceDaoHandlerDelete( session ).handle( spaceName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean renameSpace( final SpaceName spaceName, final String newName, final Session session )
    {
        try
        {
            return new SpaceDaoHandlerRename( session ).handle( spaceName, newName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
