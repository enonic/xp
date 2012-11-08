package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;


final class DeleteContentTypeDaoHandler
    extends AbstractContentTypeDaoHandler
{
    DeleteContentTypeDaoHandler( final Session session )
    {
        super( session );
    }

    int delete( final QualifiedContentTypeNames contentTypeNames )
        throws RepositoryException
    {
        int deletedCount = 0;
        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            if ( deleteContentType( contentTypeName ) )
            {
                deletedCount++;
            }
        }
        return deletedCount;
    }

    private boolean deleteContentType( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {

        final Node contentTypeNode = this.getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            return false;
        }
        else
        {
            contentTypeNode.remove();
            return true;
        }
    }
}
