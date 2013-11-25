package com.enonic.wem.core.content.attachment.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_ATTACHMENTS_NODE;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;
import static org.apache.commons.lang.StringUtils.substringBetween;


final class AttachmentDaoHandlerRename
    extends AbstractAttachmentDaoHandler
{

    AttachmentDaoHandlerRename( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    boolean handle( final ContentId contentId, String oldName, final String newName )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentId );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentId );
        }

        final Node attachmentsNode = contentNode.getNode( CONTENT_ATTACHMENTS_NODE );
        final NodeIterator attachmentNodes = attachmentsNode.getNodes();

        final String oldNameWithoutExt = substringBeforeLast( oldName, "." );
        final String newNameWithoutExt = substringBeforeLast( newName, "." );
        boolean updated = false;
        while ( attachmentNodes.hasNext() )
        {
            // old_name.jpg         => new_name.jpg
            // old_name-large.png   => new_name-large.png
            // old_name-medium.jpg  => new_name-medium.jpg
            // _thumb.png           => _thumb.png
            final Node attachmentNode = attachmentNodes.nextNode();
            final String attachmentName = attachmentNode.getName();
            if ( attachmentName.equals( oldName ) )
            {
                rename( attachmentNode, newName );
                updated = true;
                continue;
            }
            final String attachmentNameWithoutExt = substringBeforeLast( attachmentName, "." );
            final String attachmentExtension = attachmentName.contains( "." ) ? "." + substringAfterLast( attachmentName, "." ) : "";
            if ( attachmentNameWithoutExt.equals( oldName ) )
            {
                rename( attachmentNode, newNameWithoutExt + attachmentExtension );
                updated = true;
                continue;
            }

            final String infix = substringBetween( attachmentName, oldNameWithoutExt + "-", attachmentExtension );
            if ( infix != null )
            {
                final String newAttachmentName = newNameWithoutExt + ( infix.isEmpty() ? "" : "-" + infix ) + attachmentExtension;
                attachmentNode.setProperty( AttachmentJcrMapper.NAME, newAttachmentName );
                rename( attachmentNode, newAttachmentName );
                updated = true;
            }
        }
        return updated;
    }

    private void rename( final Node node, final String newName )
        throws RepositoryException
    {
        final String srcPath = node.getPath();
        final String dstPath = node.getParent().getPath() + "/" + newName;
        session.move( srcPath, dstPath );
    }

}
