package com.enonic.wem.core.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;


final class ContentTypeDaoHandlerUpdate
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerUpdate( final Session session )
    {
        super( session );
    }

    void update( final ContentType contentType )
        throws RepositoryException
    {
        final ContentTypeName contentTypeName = contentType.getQualifiedName();
        final Node contentTypeNode = getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            throw new SystemException( "Content type not found: {0}", contentTypeName.toString() );
        }

        this.contentTypeJcrMapper.toJcr( contentType, contentTypeNode );
    }

}
