package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_ATTACHMENTS_NODE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


final class AttachmentDaoHandlerCreate
    extends AbstractAttachmentDaoHandler
{
    AttachmentDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    void handle( final ContentPath contentPath, final Attachment attachment )
        throws RepositoryException
    {
        Preconditions.checkNotNull( attachment );

        final Node contentNode = doGetContentNode( contentPath );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentPath );
        }
        createAttachment( attachment, contentNode );
    }

    void handle( final ContentId contentId, final Attachment attachment )
        throws RepositoryException
    {
        Preconditions.checkNotNull( attachment );

        final Node contentNode = doGetContentNode( contentId );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentId );
        }
        createAttachment( attachment, contentNode );
    }

    private void createAttachment( final Attachment attachment, final Node contentNode )
        throws RepositoryException
    {
        final Node attachmentsNode = contentNode.getNode( CONTENT_ATTACHMENTS_NODE );
        final String name = attachment.getName();
        final Node attachmentNode;
        if ( attachmentsNode.hasNode( name ) )
        {
            attachmentNode = attachmentsNode.getNode( name );
        }
        else
        {
            attachmentNode = attachmentsNode.addNode( name, NT_UNSTRUCTURED );
        }

        attachmentJcrMapper.toJcr( attachment, attachmentNode );
    }

}
