package com.enonic.wem.core.content.dao;


import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentAlreadyExistException;
import com.enonic.wem.api.exception.ContentNotFoundException;

class ContentDaoHandlerRename
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerRename( final Session session )
    {
        super( session );
    }

    boolean handle( ContentId content, String newName )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( content );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content );
        }

        final String srcPath = contentNode.getPath();
        final String dstPath = contentNode.getParent().getPath() + "/" + newName;
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
}
