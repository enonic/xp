package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_ATTACHMENTS_NODE;


final class AttachmentDaoHandlerGet
    extends AbstractAttachmentDaoHandler
{
    AttachmentDaoHandlerGet( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    Attachment handle( final ContentPath contentPath, final String attachmentName )
        throws RepositoryException
    {
        Preconditions.checkNotNull( attachmentName );

        final Node contentNode = doGetContentNode( contentPath );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentPath );
        }
        return getAttachment( attachmentName, contentNode );
    }

    Attachment handle( final ContentId contentId, final String attachmentName )
        throws RepositoryException
    {
        Preconditions.checkNotNull( attachmentName );

        final Node contentNode = doGetContentNode( contentId );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentId );
        }
        return getAttachment( attachmentName, contentNode );
    }

    private Attachment getAttachment( final String attachmentName, final Node contentNode )
        throws RepositoryException
    {
        final Node attachmentsNode = contentNode.getNode( CONTENT_ATTACHMENTS_NODE );
        if ( attachmentsNode.hasNode( attachmentName ) )
        {
            final Node attachmentNode = attachmentsNode.getNode( attachmentName );
            final Attachment.Builder attachment = Attachment.newAttachment();
            attachmentJcrMapper.toAttachment( attachmentNode, attachment );
            return attachment.build();
        }
        else
        {
            return null;
        }
    }

}
