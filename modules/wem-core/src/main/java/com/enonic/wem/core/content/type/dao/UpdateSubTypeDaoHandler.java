package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.exception.SystemException;


final class UpdateSubTypeDaoHandler
    extends AbstractSubTypeDaoHandler
{
    UpdateSubTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void update( final SubType subType )
        throws RepositoryException
    {
        final QualifiedSubTypeName qualifiedName = subType.getQualifiedName();
        final Node subTypeNode = getSubTypeNode( qualifiedName );
        if ( subTypeNode == null )
        {
            throw new SystemException( "Sub type not found: {0}", qualifiedName.toString() );
        }

        this.subTypeJcrMapper.toJcr( subType, subTypeNode );
    }

}
