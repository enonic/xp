package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.space.Space;
import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.api.content.space.Spaces;

@Component
public final class SpaceDaoImpl
    implements SpaceDao
{

    @Override
    public Space createSpace( final Space space, final Session session )
    {
        try
        {
            return new CreateSpaceDaoHandler( session ).handle( space );
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
            return new GetSpaceDaoHandler( session ).getSpace( spaceName );
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
            return new GetSpaceDaoHandler( session ).getAllSpaces();
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
            new UpdateSpaceDaoHandler( session ).handle( space );
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
            new DeleteSpaceDaoHandler( session ).handle( spaceName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
