package com.enonic.wem.core.content.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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

    final List<ContentAndNode> doContentNodesToContentAndNodes( final NodeIterator nodeIterator )
        throws RepositoryException
    {
        List<ContentAndNode> contentList = new ArrayList<ContentAndNode>();
        while ( nodeIterator.hasNext() )
        {
            Node contentNode = nodeIterator.nextNode();
            final Content content = Content.create( ContentPath.from( contentNode.getPath() ) );
            contentJcrMapper.toContent( contentNode, content );
            contentList.add( new ContentAndNode( content, contentNode ) );
        }
        return contentList;
    }

    final NodeIterator doGetTopContentNodes( final Session session )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentsNode = JcrHelper.getNodeOrNull( rootNode, ContentDaoConstants.CONTENTS_PATH );
        return contentsNode.getNodes();
    }

    final NodeIterator doGetChildContentNodes( final Node contentParentNode )
        throws RepositoryException
    {
        return contentParentNode.getNodes();
    }

    final Node doGetContentNode( final Session session, final ContentPath contentPath )
        throws RepositoryException
    {
        final String path = getNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    final Content doFindContent( final ContentPath contentPath, final Session session )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentPath );
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

    class ContentAndNode
    {
        Content content;

        Node contentNode;

        ContentAndNode( final Content content, final Node contentNode )
        {
            this.content = content;
            this.contentNode = contentNode;
        }
    }
}
