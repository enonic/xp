package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.exception.SubTypeNotFoundException;


final class DeleteSubTypeDaoHandler
    extends AbstractSubTypeDaoHandler
{
    DeleteSubTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final QualifiedSubTypeName qualifiedSubTypeName )
        throws RepositoryException
    {
        final Node subTypeNode = getSubTypeNode( qualifiedSubTypeName );

        if ( subTypeNode == null )
        {
            throw new SubTypeNotFoundException( qualifiedSubTypeName );
        }

        subTypeNode.remove();
    }
}
