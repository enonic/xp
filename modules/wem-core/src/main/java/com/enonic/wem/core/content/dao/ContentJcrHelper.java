package com.enonic.wem.core.content.dao;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.space.SpaceName;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.core.content.dao.ContentDao.SPACES_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.SPACE_CONTENT_ROOT_NODE;
import static com.enonic.wem.core.jcr.JcrHelper.getNodeOrNull;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class ContentJcrHelper
{
    public static boolean moveContentNode( final String srcPath, final String dstPath, final Session session )
        throws RepositoryException
    {
        if ( srcPath.equals( dstPath ) )
        {
            return false;
        }

        try
        {
            session.move( srcPath, dstPath );
            return true;
        }
        catch ( ItemExistsException e )
        {
            final ContentPath path = getContentPathFromNode( session.getNode( dstPath ) );
            throw new ContentAlreadyExistException( path );
        }
    }

    public static boolean isNonContentNode( Node node )
        throws RepositoryException
    {
        return node.getName().startsWith( ContentDao.NON_CONTENT_NODE_PREFIX );
    }

    public static final Node doGetContentNode( final ContentPath contentPath, final Session session )
        throws RepositoryException
    {
        final String path = resolveNodePath( contentPath );
        final Node rootNode = session.getRootNode();
        return getNodeOrNull( rootNode, path );
    }

    public static final Node doGetContentNode( final ContentId contentId, final Session session )
        throws RepositoryException
    {
        try
        {
            return session.getNodeByIdentifier( contentId.toString() );
        }
        catch ( ItemNotFoundException | IllegalArgumentException e )
        {
            return null;
        }
    }

    public static final Content doFindContent( final ContentPath contentPath, final Session session,
                                               final ContentJcrMapper contentJcrMapper )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentPath, session );
        if ( contentNode == null )
        {
            return null;
        }

        return nodeToContent( contentNode, contentJcrMapper );
    }

    public static final Content doFindContent( final ContentId contentId, final Session session,
                                           final ContentJcrMapper contentJcrMapper )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentId, session );
        if ( contentNode == null )
        {
            return null;
        }

        return nodeToContent( contentNode, contentJcrMapper );
    }

    protected static ContentPath getContentPathFromNode( final Node node )
        throws RepositoryException
    {
        final String jcrNodePath = substringAfter( node.getPath(), SPACES_PATH );
        final String spaceName = substringBefore( jcrNodePath, "/" + SPACE_CONTENT_ROOT_NODE );
        final String contentPath = substringAfter( jcrNodePath, "/" + SPACE_CONTENT_ROOT_NODE );
        return ContentPath.from( spaceName + ":" + contentPath );
    }

    protected static String resolveNodePath( final ContentPath contentPath )
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

    protected static String getSpaceRootPath( final SpaceName spaceName )
    {
        return SPACES_PATH + spaceName.name() + "/" + SPACE_CONTENT_ROOT_NODE;
    }

    protected static Content nodeToContent( final Node contentNode, final ContentJcrMapper contentJcrMapper )
        throws RepositoryException
    {
        final Content.Builder contentBuilder = newContent();
        contentJcrMapper.toContent( contentNode, contentBuilder );
        return contentBuilder.build();
    }

//    new method? getParentContentNode
//    and any methods that methods above are dependent on
}
