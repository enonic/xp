package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;

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
            throw new UnableToDeleteContentException( pathToContent, "Content has child content." );
        }

        contentNode.remove();
    }
}
