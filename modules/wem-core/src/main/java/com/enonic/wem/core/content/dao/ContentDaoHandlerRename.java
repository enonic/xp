package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;

class ContentDaoHandlerRename
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerRename( final Session session )
    {
        super( session );
    }

    void handle( ContentPath content, String newName )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, content );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content );
        }
        session.move( contentNode.getPath(), contentNode.getParent().getPath() + "/" + newName );
    }
}
