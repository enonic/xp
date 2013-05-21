package com.enonic.wem.core.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;


final class ContentTypeDaoHandlerDelete
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    void handle( final QualifiedContentTypeName qualifiedContentTypeName )
        throws RepositoryException
    {
        final Node contentTypeNode = getContentTypeNode( qualifiedContentTypeName );

        if ( contentTypeNode == null )
        {
            throw new ContentTypeNotFoundException( qualifiedContentTypeName );
        }

        contentTypeNode.remove();
    }

}
