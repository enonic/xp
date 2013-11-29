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
    public Content create( final Content content, final Session session )
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
    public Content update( final Content content, final boolean createNewVersion, final Session session )
    {
        try
        {
            return new ContentDaoHandlerUpdate( session, this.indexService ).handle( content, createNewVersion );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteById( final ContentId contentId, final Session session )
        throws ContentNotFoundException, UnableToDeleteContentException
    {
        try
        {
            new ContentDaoHandlerDelete( session, this.indexService ).deleteContentById( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteByPath( final ContentPath contentPath, final Session session )
        throws ContentNotFoundException, UnableToDeleteContentException
    {
        try
        {
            if ( contentPath.isRoot() )
            {
                throw new UnableToDeleteContentException( contentPath, "Root content." );
            }

            new ContentDaoHandlerDelete( session, this.indexService ).deleteContentByPath( contentPath );
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

    public Content selectById( final ContentId contentId, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGet( session, this.indexService ).findContentById( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    public Content selectByPath( final ContentPath contentPath, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGet( session, this.indexService ).findContentByPath( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    public Contents selectByIds( final ContentIds contentIds, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGet( session, this.indexService ).findContentsById( contentIds );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    public Contents selectByPaths( final ContentPaths contentPaths, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGet( session, this.indexService ).findContentsByPath( contentPaths );
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
    public List<ContentVersion> getContentVersionsById( final ContentId contentId, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGetContentVersionHistory( session, this.indexService ).handle( contentId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<ContentVersion> getContentVersionsByPath( final ContentPath contentPath, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGetContentVersionHistory( session, this.indexService ).handle( contentPath );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content getContentVersionById( final ContentId contentId, final ContentVersionId versionId, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGetVersion( session, this.indexService ).handle( contentId, versionId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Content getContentVersionByPath( final ContentPath contentPath, final ContentVersionId versionId, final Session session )
    {
        try
        {
            return new ContentDaoHandlerGetVersion( session, this.indexService ).handle( contentPath, versionId );
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
