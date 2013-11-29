package com.enonic.wem.core.content.attachment.dao;


import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.index.IndexService;

public class AttachmentDaoImpl
    implements AttachmentDao
{

    private IndexService indexService;

    @Override
    public void createAttachmentById( final ContentId contentId, final Attachment attachment, final Session session )
    {
        try
        {
            new AttachmentDaoHandlerCreate( session, this.indexService ).handle( contentId, attachment );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void createAttachmentByPath( final ContentPath contentPath, final Attachment attachment, final Session session )
    {
        try
        {
            new AttachmentDaoHandlerCreate( session, this.indexService ).handle( contentPath, attachment );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Attachment getAttachmentById( final ContentId contentId, final String attachmentName, final Session session )
    {
        try
        {
            return new AttachmentDaoHandlerGet( session, this.indexService ).handle( contentId, attachmentName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Attachment getAttachmentByPath( final ContentPath contentPath, final String attachmentName, final Session session )
    {
        try
        {
            return new AttachmentDaoHandlerGet( session, this.indexService ).handle( contentPath, attachmentName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean deleteAttachmentById( final ContentId contentId, final String attachmentName, final Session session )
    {
        try
        {
            return new AttachmentDaoHandlerDelete( session, this.indexService ).handle( contentId, attachmentName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean deleteAttachmentByPath( final ContentPath contentPath, final String attachmentName, final Session session )
    {
        try
        {
            return new AttachmentDaoHandlerDelete( session, this.indexService ).handle( contentPath, attachmentName );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean renameAttachments( final ContentId contentId, String oldContentName, final String newContentName, final Session session )
    {
        try
        {
            return new AttachmentDaoHandlerRename( session, this.indexService ).handle( contentId, oldContentName, newContentName );
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
