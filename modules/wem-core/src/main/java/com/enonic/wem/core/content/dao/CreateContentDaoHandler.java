package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentAlreadyExistException;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;
import static com.enonic.wem.core.content.dao.ContentDao.SPACES_PATH;
import static com.enonic.wem.core.content.dao.ContentDao.SPACE_CONTENT_ROOT_NODE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


final class CreateContentDaoHandler
    extends AbstractContentDaoHandler
{
    CreateContentDaoHandler( final Session session )
    {
        super( session );
    }

    ContentId handle( final Content content )
        throws RepositoryException
    {
        if ( content.getId() != null )
        {
            throw new IllegalArgumentException( "Attempt to create new content with assigned id: " + content.getId() );
        }
        final ContentPath path = content.getPath();
        Preconditions.checkArgument( path.isAbsolute(), "Content path must be absolute: " + path.toString() );

        final Node root = session.getRootNode();
        final String spaceNodePath = SPACES_PATH + path.getSpace().name();
        if ( !root.hasNode( spaceNodePath ) )
        {
            throw new SpaceNotFoundException( path.getSpace() );
        }

        final Node newContentNode;
        final String spaceRootPath = getSpaceRootPath( path.getSpace() );
        if ( path.isRoot() )
        {
            if ( root.hasNode( spaceRootPath ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            final Node spaceNode = root.getNode( spaceNodePath );

            newContentNode = addContentToJcr( content, spaceNode );
        }
        else if ( path.elementCount() == 1 )
        {
            final Node contentsNode = JcrHelper.getNodeOrNull( root, spaceRootPath );
            if ( contentsNode == null )
            {
                throw new ContentNotFoundException( ContentPath.rootOf( path.getSpace() ) );
            }
            if ( contentsNode.hasNode( path.getName() ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, contentsNode );
        }
        else
        {
            final Node parentContentNode = doGetContentNode( session, path.getParentPath() );
            if ( parentContentNode == null )
            {
                throw new ContentNotFoundException( path.getParentPath() );
            }
            else if ( parentContentNode.hasNode( path.getName() ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, parentContentNode );
        }
        final Node contentVersionHistoryParent = createContentVersionHistory( content, newContentNode );
        addContentVersion( content, contentVersionHistoryParent );
        return ContentIdFactory.from( newContentNode );
    }

    private Node addContentToJcr( final Content content, final Node parentNode )
        throws RepositoryException
    {
        final String nodeName = content.getName() == null ? SPACE_CONTENT_ROOT_NODE : content.getName();
        final Node newContentNode = parentNode.addNode( nodeName, JcrConstants.CONTENT_TYPE );
        contentJcrMapper.toJcr( content, newContentNode );
        return newContentNode;
    }

    private Node createContentVersionHistory( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionNode = contentNode.addNode( CONTENT_VERSION_HISTORY_NODE, NT_UNSTRUCTURED );
        contentVersionNode.setProperty( CONTENT_NEXT_VERSION_PROPERTY, content.getVersionId().id() + 1 );
        return contentVersionNode;
    }
}
