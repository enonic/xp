package com.enonic.wem.core.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeName;


final class ContentTypeDaoHandlerDelete
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    void handle( final ContentTypeName contentTypeName )
        throws RepositoryException
    {
        final Node contentTypeNode = getContentTypeNode( contentTypeName );

        if ( contentTypeNode == null )
        {
            throw new ContentTypeNotFoundException( contentTypeName );
        }

        contentTypeNode.remove();
    }

}
