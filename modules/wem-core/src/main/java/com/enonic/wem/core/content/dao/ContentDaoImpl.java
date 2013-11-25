package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.index.IndexService;


/**
 * TODO: Figure out how to handle the thrown RepositoryException from JCR.
 */

@Singleton
public class ContentDaoImpl
    implements ContentDao
{
    private IndexService indexService;

    @Override
    public ContentId create( final Content content, final Session session )
    {
        try
        {
            return new ContentDaoHandlerCreate( session, indexService ).handle( content );
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
            new ContentDaoHandlerUpdate( session, this.indexService ).handle( content, createNewVersion );
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

                new ContentDaoHandlerDelete( session, this.indexService ).deleteContentByPath( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                new ContentDaoHandlerDelete( session, this.indexService ).deleteContentById( contentId );
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
    public void forceDelete( final ContentId contentId, final Session session )
        throws ContentNotFoundException
    {
        try
        {
            new ContentDaoHandlerDelete( session, this.indexService ).force( true ).ignoreContentNotFound( true ).deleteContentById(
                contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean renameContent( final ContentId contentId, final String newName, final Session session )
    {
        try
        {
            return new ContentDaoHandlerRename( session, this.indexService ).handle( contentId, newName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void moveContent( final ContentId contentId, final ContentPath newPath, final Session session )
    {
        try
        {
            new ContentDaoHandlerMove( session, this.indexService ).handle( contentId, newPath );
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
                return new ContentDaoHandlerGet( session, this.indexService ).findContentByPath( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerGet( session, this.indexService ).findContentById( contentId );
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
                return new ContentDaoHandlerGet( session, this.indexService ).findContentsByPath( contentPaths );
            }
            else if ( contentSelectors instanceof ContentIds )
            {
                final ContentIds contentIds = (ContentIds) contentSelectors;
                return new ContentDaoHandlerGet( session, this.indexService ).findContentsById( contentIds );
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
            return new ContentDaoHandlerGetChild( session, this.indexService ).handle( parentPath );
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
            return new ContentDaoHandlerGetContentTree( session, this.indexService ).handle();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public int countContentTypeUsage( final ContentTypeName qualifiedContentTypeName, Session session )
    {
        try
        {
            return new ContentDaoHandlerCountContentTypeUsage( session, indexService ).handle( qualifiedContentTypeName );
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
                return new ContentDaoHandlerGetContentVersionHistory( session, this.indexService ).handle( contentPath );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerGetContentVersionHistory( session, this.indexService ).handle( contentId );
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
                return new ContentDaoHandlerGetVersion( session, this.indexService ).handle( contentPath, versionId );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new ContentDaoHandlerGetVersion( session, this.indexService ).handle( contentId, versionId );
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

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
