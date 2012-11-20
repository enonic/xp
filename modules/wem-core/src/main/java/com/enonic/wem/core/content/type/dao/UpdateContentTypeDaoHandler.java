package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.exception.SystemException;


final class UpdateContentTypeDaoHandler
    extends AbstractContentTypeDaoHandler
{
    UpdateContentTypeDaoHandler( final Session session )
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
