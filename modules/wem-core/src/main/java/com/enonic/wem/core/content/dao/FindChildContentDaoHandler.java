package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

class FindChildContentDaoHandler
    extends AbstractContentDaoHandler
{
    FindChildContentDaoHandler( final Session session )
    {
        super( session );
    }

    Contents handle( final ContentPath parentPath )
        throws RepositoryException
    {
        Contents.Builder contentsBuilder = Contents.builder();
        Node parentContentNode = getContentNode( session, parentPath );
        //parentContentNode.

        return contentsBuilder.build();
    }
}

