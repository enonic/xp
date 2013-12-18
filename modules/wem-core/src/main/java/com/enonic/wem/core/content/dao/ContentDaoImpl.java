package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.ContentVersionId;
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
