package com.enonic.wem.core.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.jcr.JcrHelper;

class AbstractContentDaoHandler
{
    final Session session;

    final ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    AbstractContentDaoHandler( final Session session )
    {
        this.session = session;
    }

    Node getContentNode( final Session session, final ContentPath contentPath )
        throws RepositoryException
    {
        final String path = getNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    Content doFindContent( final ContentPath contentPath, final Session session )
        throws RepositoryException
    {
        final Node contentNode = getContentNode( session, contentPath );
        if ( contentNode == null )
        {
            return null;
        }

        final Content content = Content.create( contentPath );
        contentJcrMapper.toContent( contentNode, content );
        return content;

    }

    private String getNodePath( final ContentPath contentPath )
    {
        return ContentDaoConstants.CONTENTS_PATH + contentPath.toString();
    }
}
