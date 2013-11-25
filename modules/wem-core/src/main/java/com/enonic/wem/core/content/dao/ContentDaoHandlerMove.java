package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.index.IndexService;

class ContentDaoHandlerMove
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerMove( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    void handle( ContentId content, ContentPath newPath )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( content );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content );
        }

        final String srcPath = contentNode.getPath();
        final String dstPath = "/" + resolveNodePath( newPath );
        moveContentNode( srcPath, dstPath );
    }
}
