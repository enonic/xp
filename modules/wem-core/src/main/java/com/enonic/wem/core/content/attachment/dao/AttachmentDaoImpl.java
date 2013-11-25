package com.enonic.wem.core.content.attachment.dao;


import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.index.IndexService;

public class AttachmentDaoImpl
    implements AttachmentDao
{

    private IndexService indexService;

    @Override
    public void createAttachment( final ContentSelector contentSelector, final Attachment attachment, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                new AttachmentDaoHandlerCreate( session, this.indexService ).handle( contentPath, attachment );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                new AttachmentDaoHandlerCreate( session, this.indexService ).handle( contentId, attachment );
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
    public Attachment getAttachment( final ContentSelector contentSelector, final String attachmentName, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                return new AttachmentDaoHandlerGet( session, this.indexService ).handle( contentPath, attachmentName );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new AttachmentDaoHandlerGet( session, this.indexService ).handle( contentId, attachmentName );
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
    public boolean deleteAttachment( final ContentSelector contentSelector, final String attachmentName, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                return new AttachmentDaoHandlerDelete( session, this.indexService ).handle( contentPath, attachmentName );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new AttachmentDaoHandlerDelete( session, this.indexService ).handle( contentId, attachmentName );
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
