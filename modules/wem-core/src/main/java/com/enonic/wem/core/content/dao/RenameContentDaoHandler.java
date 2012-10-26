package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentPath;

class RenameContentDaoHandler
    extends AbstractContentDaoHandler
{
    RenameContentDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( ContentPath content, String newName )
        throws RepositoryException
    {
        final Node contentNode = getContentNode( session, content );
        if ( contentNode == null )
        {
            // TODO: Replace with better exception
            throw new RuntimeException( "Content to rename does not exist: " + content.toString() );
        }
        session.move( contentNode.getPath(), contentNode.getParent().getPath() + "/" + newName );
    }
}
