package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.versioning.ContentVersionId;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_PREFIX;

final class ContentDaoHandlerGetVersion
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerGetVersion( final Session session )
    {
        super( session );
    }

    Content handle( final ContentId contentId, final ContentVersionId versionId )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentId );
        return getContentFromNode( contentNode, versionId );
    }

    Content handle( final ContentPath contentPath, final ContentVersionId versionId )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentPath );
        return getContentFromNode( contentNode, versionId );
    }

    Content getContentFromNode( final Node contentNode, final ContentVersionId versionId )
        throws RepositoryException
    {
        if ( contentNode == null )
        {
            return null;
        }
        final Node contentVersionParent = getContentVersionHistoryNode( contentNode );
        final String contentVersionNodeName = CONTENT_VERSION_PREFIX + versionId.id();
        if ( !contentVersionParent.hasNode( contentVersionNodeName ) )
        {
            return null;
        }
        final Node contentVersionNode = contentVersionParent.getNode( contentVersionNodeName );
        final Content.Builder contentBuilder = newContent();
        contentJcrMapper.toContent( contentVersionNode, contentBuilder );
        return contentBuilder.build();
    }
}