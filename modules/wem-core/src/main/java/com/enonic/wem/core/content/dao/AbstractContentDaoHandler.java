package com.enonic.wem.core.content.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.jcr.JcrConstants;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_PREFIX;
import static com.enonic.wem.core.content.dao.ContentDao.SPACES_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.SPACE_CONTENT_ROOT_NODE;
import static com.enonic.wem.core.jcr.JcrHelper.getNodeOrNull;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang.StringUtils.substringAfter;

abstract class AbstractContentDaoHandler
{
    protected final Session session;

    protected final ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    AbstractContentDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final List<ContentAndNode> doContentNodesToContentAndNodes( final Iterator<Node> nodeIterator )
        throws RepositoryException
    {
        List<ContentAndNode> contentList = new ArrayList<ContentAndNode>();
        while ( nodeIterator.hasNext() )
        {
            final Node contentNode = nodeIterator.next();
            if ( contentNode.getName().equals( CONTENT_VERSION_HISTORY_NODE ) )
            {
                continue;
            }
            final String jcrNodePath = contentNode.getPath();
            final String contentPath = substringAfter( jcrNodePath, SPACES_PATH );
            final Content.Builder contentBuilder = newContent().path( ContentPath.from( contentPath ) );
            contentJcrMapper.toContent( contentNode, contentBuilder );
            contentList.add( new ContentAndNode( contentBuilder.build(), contentNode ) );
        }
        return contentList;
    }

    protected final Iterator<Node> doGetTopContentNodes( final Session session )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentsNode = getNodeOrNull( rootNode, SPACES_PATH );

        final List<Node> topNodes = Lists.newArrayList();
        final NodeIterator spaceNodesIterator = contentsNode.getNodes();
        while ( spaceNodesIterator.hasNext() )
        {
            final Node spaceNode = spaceNodesIterator.nextNode();
            topNodes.add( spaceNode.getNode( SPACE_CONTENT_ROOT_NODE ) );
        }
        return topNodes.iterator();
    }

    protected final NodeIterator doGetChildContentNodes( final Node contentParentNode )
        throws RepositoryException
    {
        return contentParentNode.getNodes();
    }

    protected final Node doGetContentNode( final Session session, final ContentPath contentPath )
        throws RepositoryException
    {
        final String path = getNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return getNodeOrNull( rootNode, path );
    }

    protected final Node doGetContentNode( final Session session, final ContentId contentId )
        throws RepositoryException
    {
        try
        {
            return session.getNodeByIdentifier( contentId.id() );
        }
        catch ( ItemNotFoundException e )
        {
            return null;
        }
    }

    protected final Content doFindContent( final ContentPath contentPath, final Session session )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentPath );
        if ( contentNode == null )
        {
            return null;
        }

        final Content.Builder contentBuilder = newContent();
        contentJcrMapper.toContent( contentNode, contentBuilder );
        return contentBuilder.build();

    }

    protected final Content doFindContent( final ContentId contentId, final Session session )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentId );
        if ( contentNode == null )
        {
            return null;
        }

        final Content.Builder contentBuilder = newContent();
        contentJcrMapper.toContent( contentNode, contentBuilder );
        return contentBuilder.build();

    }

    private String getNodePath( final ContentPath contentPath )
    {
        if ( contentPath.isRoot() )
        {
            return getSpaceRootPath( contentPath.getSpace() );
        }
        else
        {
            final String relativePathToContent = contentPath.getRelativePath();
            return getSpaceRootPath( contentPath.getSpace() ) + "/" + removeStart( relativePathToContent, "/" );
        }
    }

    protected String getSpaceRootPath( final SpaceName spaceName )
    {
        return SPACES_PATH + spaceName.name() + "/" + SPACE_CONTENT_ROOT_NODE;
    }

    protected Node getContentVersionHistoryNode( final Node contentNode )
        throws RepositoryException
    {
        return contentNode.getNode( CONTENT_VERSION_HISTORY_NODE );
    }

    protected Node addContentVersion( final Content content, final Node contentVersionParent )
        throws RepositoryException
    {
        final String nodeVersionName = CONTENT_VERSION_PREFIX + content.getVersionId().id();
        final Node contentVersionNode = contentVersionParent.addNode( nodeVersionName, JcrConstants.CONTENT_TYPE );
        contentJcrMapper.toJcr( content, contentVersionNode );
        return contentVersionNode;
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
