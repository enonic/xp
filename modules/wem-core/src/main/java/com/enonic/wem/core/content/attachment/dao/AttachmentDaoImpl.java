package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;

public class AttachmentDaoImpl
    implements AttachmentDao
{

    @Override
    public void createAttachment( final ContentSelector contentSelector, final Attachment attachment, final Session session )
    {
        try
        {
            if ( contentSelector instanceof ContentPath )
            {
                final ContentPath contentPath = (ContentPath) contentSelector;
                new AttachmentDaoHandlerCreate( session ).handle( contentPath, attachment );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                new AttachmentDaoHandlerCreate( session ).handle( contentId, attachment );
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
                return new AttachmentDaoHandlerGet( session ).handle( contentPath, attachmentName );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new AttachmentDaoHandlerGet( session ).handle( contentId, attachmentName );
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
                return new AttachmentDaoHandlerDelete( session ).handle( contentPath, attachmentName );
            }
            else if ( contentSelector instanceof ContentId )
            {
                final ContentId contentId = (ContentId) contentSelector;
                return new AttachmentDaoHandlerDelete( session ).handle( contentId, attachmentName );
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
