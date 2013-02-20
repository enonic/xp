package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.exception.UnableToDeleteContentException;
import com.enonic.wem.api.support.tree.Tree;


/**
 * TODO: Figure out how to handle the thrown RepositoryException from JCR.
 */
@Component
public class ContentDaoImpl
    implements ContentDao
{

    @Override
    public ContentId create( final Content content, final Session session )
    {
        try
        {
            return new ContentDaoHandlerCreate( session ).handle( content );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void update( final Content content, final boolean createNewVersion, final Session session )
    {
        try
        {
            new ContentDaoHandlerUpdate( session ).handle( content, createNewVersion );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void delete( final ContentSelector contentSelector, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;

                if ( contentPath.isRoot() )
                {
                    throw new UnableToDeleteContentException( contentPath, "Root content of a space." );
                }

                new ContentDaoHandlerDelete( session ).deleteContentByPath( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                new ContentDaoHandlerDelete( session ).deleteContentById( contentId );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported content selector: " + contentSelector.getClass().getCanonicalName() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void renameContent( final ContentPath contentPath, final String newName, final Session session )
    {
        try
        {
            new ContentDaoHandlerRename( session ).handle( contentPath, newName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content select( final ContentSelector contentSelector, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                return new ContentDaoHandlerFind( session ).findContentByPath( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerFind( session ).findContentById( contentId );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported content selector: " + contentSelector.getClass().getCanonicalName() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents select( final ContentSelectors contentSelectors, final Session session )
    {
        try
        {
            if ( contentSelectors instanceof ContentPaths )
            {
                final ContentPaths contentPaths = (ContentPaths) contentSelectors;
                return new ContentDaoHandlerFind( session ).findContentsByPath( contentPaths );
            }
            else if ( contentSelectors instanceof ContentIds )
            {
                final ContentIds contentIds = (ContentIds) contentSelectors;
                return new ContentDaoHandlerFind( session ).findContentsById( contentIds );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported content selector: " + contentSelectors.getClass().getCanonicalName() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Contents findChildContent( final ContentPath parentPath, final Session session )
    {
        try
        {
            return new ContentDaoHandlerFindChild( session ).handle( parentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Tree<Content> getContentTree( final Session session )
    {
        try
        {
            return new ContentDaoHandlerGetContentTree( session ).handle();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public int countContentTypeUsage( final QualifiedContentTypeName qualifiedContentTypeName, Session session )
    {
        try
        {
            return new ContentDaoHandlerCountContentTypeUsage( session ).handle( qualifiedContentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<ContentVersion> getContentVersions( final ContentSelector contentSelector, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                return new ContentDaoHandlerGetContentVersionHistory( session ).handle( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerGetContentVersionHistory( session ).handle( contentId );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported content selector: " + contentSelector.getClass().getCanonicalName() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content getContentVersion( final ContentSelector contentSelector, final ContentVersionId versionId, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                return new ContentDaoHandlerGetVersion( session ).handle( contentPath, versionId );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerGetVersion( session ).handle( contentId, versionId );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported content selector: " + contentSelector.getClass().getCanonicalName() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
