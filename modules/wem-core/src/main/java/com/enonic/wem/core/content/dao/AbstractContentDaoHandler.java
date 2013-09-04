package com.enonic.wem.core.content.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import static org.apache.commons.lang.StringUtils.substringAfter;

public abstract class AbstractContentDaoHandler
{
    protected final Session session;

    protected final ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    protected AbstractContentDaoHandler( final Session session )
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
            if ( isNonContentNode( contentNode ) )
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

    protected final Iterator<Node> doGetTopContentNodes()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentsNode = getNodeOrNull( rootNode, SPACES_PATH );

        final List<Node> topNodes = Lists.newArrayList();

        if ( contentsNode != null )
        {
            final NodeIterator spaceNodesIterator = contentsNode.getNodes();
            while ( spaceNodesIterator.hasNext() )
            {
                final Node spaceNode = spaceNodesIterator.nextNode();
                if ( SpaceName.temporary().name().equals( spaceNode.getName() ) )
                {
                    continue; // skip space for temporary content
                }
                topNodes.add( spaceNode.getNode( SPACE_CONTENT_ROOT_NODE ) );
            }
        }

        return topNodes.iterator();
    }

    protected final NodeIterator doGetChildContentNodes( final Node contentParentNode )
        throws RepositoryException
    {
        return contentParentNode.getNodes();
    }

    protected final Node doGetContentNode( final ContentPath contentPath )
        throws RepositoryException
    {
        return ContentJcrHelper.doGetContentNode( contentPath, session );
    }

    protected final Node doGetContentNode( final ContentId contentId )
        throws RepositoryException
    {
        return ContentJcrHelper.doGetContentNode( contentId, session );
    }

    protected final Content doFindContent( final ContentPath contentPath )
        throws RepositoryException
    {
        return ContentJcrHelper.doFindContent( contentPath, session, contentJcrMapper );
    }

    protected final Content doFindContent( final ContentId contentId )
        throws RepositoryException
    {
        return ContentJcrHelper.doFindContent( contentId, session, contentJcrMapper );
    }

    protected Node getContentVersionHistoryNode( final Node contentNode )
        throws RepositoryException
    {
        return contentNode.getNode( CONTENT_VERSION_HISTORY_NODE );
    }

    protected String resolveNodePath( final ContentPath contentPath )
    {
        return ContentJcrHelper.resolveNodePath( contentPath );
    }

    public boolean moveContentNode( final String srcPath, final String dstPath )
        throws RepositoryException
    {
        return ContentJcrHelper.moveContentNode( srcPath, dstPath, session );
    }

    protected Node addContentVersion( final Content content, final Node contentVersionHistoryNode )
        throws RepositoryException
    {
        final String nodeVersionName = CONTENT_VERSION_PREFIX + content.getVersionId().id();
        final Node contentVersionNode = contentVersionHistoryNode.addNode( nodeVersionName, JcrConstants.CONTENT_NODETYPE );
        contentJcrMapper.toJcr( content, contentVersionNode );
        return contentVersionNode;
    }

    protected boolean isNonContentNode( Node node )
        throws RepositoryException
    {
        return ContentJcrHelper.isNonContentNode( node );
    }

    protected String getSpaceRootPath( final SpaceName spaceName )
    {
        return ContentJcrHelper.getSpaceRootPath( spaceName );
    }

    protected Content nodeToContent( final Node contentNode )
        throws RepositoryException
    {
        return ContentJcrHelper.nodeToContent( contentNode, contentJcrMapper );
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
