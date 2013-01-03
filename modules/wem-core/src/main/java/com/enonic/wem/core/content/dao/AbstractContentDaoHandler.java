package com.enonic.wem.core.content.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENTS_PATH;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_VERSION_HISTORY_PATH;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_VERSION_PREFIX;

abstract class AbstractContentDaoHandler
{
    protected final Session session;

    protected final ContentJcrMapper contentJcrMapper = new ContentJcrMapper();

    AbstractContentDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final List<ContentAndNode> doContentNodesToContentAndNodes( final NodeIterator nodeIterator )
        throws RepositoryException
    {
        List<ContentAndNode> contentList = new ArrayList<ContentAndNode>();
        while ( nodeIterator.hasNext() )
        {
            final Node contentNode = nodeIterator.nextNode();
            final String jcrNodePath = contentNode.getPath();
            final String contentPath = StringUtils.substringAfter( jcrNodePath, CONTENTS_PATH );
            final Content.Builder contentBuilder = newContent().path( ContentPath.from( contentPath ) );
            contentJcrMapper.toContent( contentNode, contentBuilder );
            contentList.add( new ContentAndNode( contentBuilder.build(), contentNode ) );
        }
        return contentList;
    }

    protected final NodeIterator doGetTopContentNodes( final Session session )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentsNode = JcrHelper.getNodeOrNull( rootNode, CONTENTS_PATH );
        return contentsNode.getNodes();
    }

    protected final NodeIterator doGetChildContentNodes( final Node contentParentNode )
        throws RepositoryException
    {
        return contentParentNode.getNodes();
    }

    protected final Node doGetContentNode( final Session session, final ContentPath contentPath )
        throws RepositoryException
    {
        if ( contentPath.isRoot() )
        {
            return null;
        }
        final String path = getNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
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
        final String relativePathToContent = StringUtils.removeStart( contentPath.toString(), "/" );
        return CONTENTS_PATH + relativePathToContent;
    }

    protected Node getContentVersionHistory( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node parent = contentNode.getParent();
        final String parentPath = CONTENT_VERSION_HISTORY_PATH + StringUtils.substringAfter( parent.getPath(), CONTENTS_PATH );
        return session.getNode( "/" + parentPath ).getNode( content.getName() );
    }

    protected Node addContentVersion( final Content content, final Node contentVersionParent )
        throws RepositoryException
    {
        final long versionId = getNextContentVersion( contentVersionParent );
        final String nodeVersionName = CONTENT_VERSION_PREFIX + versionId;
        final Node contentVersionNode = contentVersionParent.addNode( nodeVersionName, JcrConstants.CONTENT_TYPE );
        contentJcrMapper.toJcr( content, contentVersionNode );
        return contentVersionNode;
    }

    private long getNextContentVersion( final Node contentVersionParent )
        throws RepositoryException
    {
        final long versionNumber = JcrHelper.getPropertyLong( contentVersionParent, CONTENT_NEXT_VERSION_PROPERTY, 0l );
        contentVersionParent.setProperty( CONTENT_NEXT_VERSION_PROPERTY, versionNumber + 1 );
        return versionNumber;
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
