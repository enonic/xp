package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentAlreadyExistException;
import com.enonic.wem.api.exception.ContentNotFoundException;


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
        final Node contentsNode = root.getNode( ContentDaoConstants.CONTENTS_PATH );
        final ContentPath path = content.getPath();

        final Node newContentNode;
        if ( path.elementCount() == 1 )
        {
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
        return ContentIdImpl.from( newContentNode );
    }

    private Node addContentToJcr( final Content content, final Node parentNode )
        throws RepositoryException
    {
        final Node newContentNode = parentNode.addNode( content.getName() );
        contentJcrMapper.toJcr( content, newContentNode );
        return newContentNode;
    }
}
