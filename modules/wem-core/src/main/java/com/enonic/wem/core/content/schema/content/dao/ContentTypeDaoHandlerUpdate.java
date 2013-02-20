package com.enonic.wem.core.content.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.exception.SystemException;


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
        final QualifiedContentTypeName contentTypeName = contentType.getQualifiedName();
        final Node contentTypeNode = getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            throw new SystemException( "Content type not found: {0}", contentTypeName.toString() );
        }

        this.contentTypeJcrMapper.toJcr( contentType, contentTypeNode );
    }

}
