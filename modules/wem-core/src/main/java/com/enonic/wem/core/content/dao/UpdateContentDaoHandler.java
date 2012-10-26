package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;

class UpdateContentDaoHandler
    extends AbstractContentDaoHandler
{
    UpdateContentDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( Content content )
        throws RepositoryException
    {
        final Node contentNode = getContentNode( session, content.getPath() );
        if ( contentNode == null )
        {
            // TODO: Replace with better exception
            throw new RuntimeException( "Content to update does not exist: " + content.getPath() );
        }
        contentJcrMapper.toJcr( content, contentNode );
    }
}
