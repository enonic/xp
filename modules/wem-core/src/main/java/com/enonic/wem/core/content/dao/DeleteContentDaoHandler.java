package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;

final class DeleteContentDaoHandler
    extends AbstractContentDaoHandler
{
    DeleteContentDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final ContentPath pathToContent )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, pathToContent );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( pathToContent );
        }

        if ( contentNode.hasNodes() )
        {
            throw new IllegalArgumentException( "Content with sub content cannot be deleted: " + pathToContent );
        }

        contentNode.remove();
    }
}
