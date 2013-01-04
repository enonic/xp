package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentAlreadyExistException;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.core.jcr.JcrConstants;

import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENTS_PATH;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_VERSION_HISTORY_PATH;
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

        final Node root = session.getRootNode();
        final Node contentsNode = root.getNode( CONTENTS_PATH );
        final ContentPath path = content.getPath();

        final Node newContentNode;
        if ( path.elementCount() == 1 )
        {
            if ( contentsNode.hasNode( path.getName() ) )
            {
                throw new ContentAlreadyExistException( path );
            }
            newContentNode = addContentToJcr( content, contentsNode );
            final Node contentVersionHistoryParent = createContentVersionHistory( content, contentsNode );
            addContentVersion( content, contentVersionHistoryParent );
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
            final Node contentVersionHistoryParent = createContentVersionHistory( content, parentContentNode );
            addContentVersion( content, contentVersionHistoryParent );
        }
        return ContentIdImpl.from( newContentNode );
    }

    private Node addContentToJcr( final Content content, final Node parentNode )
        throws RepositoryException
    {
        final Node newContentNode = parentNode.addNode( content.getName(), JcrConstants.CONTENT_TYPE );
        contentJcrMapper.toJcr( content, newContentNode );
        return newContentNode;
    }

    private Node createContentVersionHistory( final Content content, final Node parentNode )
        throws RepositoryException
    {
        final String parentPath = CONTENT_VERSION_HISTORY_PATH + StringUtils.substringAfter( parentNode.getPath(), CONTENTS_PATH );
        final Node contentVersionNode = session.getNode( "/" + parentPath ).addNode( content.getName(), NT_UNSTRUCTURED );
        contentVersionNode.setProperty( CONTENT_NEXT_VERSION_PROPERTY, content.getVersionId().id() + 1 );
        return contentVersionNode;
    }
}
