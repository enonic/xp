package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.exception.ContentNotFoundException;

final class UpdateContentDaoHandler
    extends AbstractContentDaoHandler
{
    UpdateContentDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( Content content, final boolean createNewVersion )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, content.getPath() );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content.getPath() );
        }
        contentJcrMapper.toJcr( content, contentNode );

        if ( createNewVersion )
        {
            final Node contentVersionHistoryParent = getContentVersionHistory( content, contentNode );
            addContentVersion( content, contentVersionHistoryParent );
        }
    }
}
