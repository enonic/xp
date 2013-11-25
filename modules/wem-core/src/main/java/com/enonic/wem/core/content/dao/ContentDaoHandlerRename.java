package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.core.index.IndexService;

class ContentDaoHandlerRename
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerRename( final Session session, final IndexService indexService )
    {
        super( session, indexService );
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
        return moveContentNode( srcPath, dstPath );
    }
}
