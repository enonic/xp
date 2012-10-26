package com.enonic.wem.core.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;


/**
 * TODO: Figure out how to handle the thrown RepositoryException from JCR.
 */
@Component
public class ContentDaoImpl
    implements ContentDao
{

    @Override
    public void createContent( final Content content, final Session session )
    {
        try
        {
            new CreateContentDaoHandler( session ).handle( content );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void updateContent( final Content content, final Session session )
    {
        try
        {
            new UpdateContentDaoHandler( session ).handle( content );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void renameContent( final ContentPath content, final String newName, final Session session )
    {
        try
        {
            new RenameContentDaoHandler( session ).handle( content, newName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content findContent( final ContentPath contentPath, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).handle( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents findContent( final ContentPaths contentPaths, final Session session )
    {
        try
        {
            return new FindContentDaoHandler( session ).handle( contentPaths );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
