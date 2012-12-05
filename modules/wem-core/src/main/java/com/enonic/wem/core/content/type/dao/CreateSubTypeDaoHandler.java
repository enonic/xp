package com.enonic.wem.core.content.type.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.JcrHelper;


final class CreateSubTypeDaoHandler
    extends AbstractSubTypeDaoHandler
{
    CreateSubTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void create( final SubType subType )
        throws RepositoryException
    {
        final QualifiedSubTypeName qualifiedName = subType.getQualifiedName();
        if ( subTypeExists( qualifiedName ) )
        {
            throw new SystemException( "Sub type already exists: {0}", qualifiedName.toString() );
        }

        final Node subTypeNode = createSubTypeNode( qualifiedName );
        this.subTypeJcrMapper.toJcr( subType, subTypeNode );
    }

    private Node createSubTypeNode( final QualifiedSubTypeName qualifiedName )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node subTypesNode = rootNode.getNode( SubTypeDao.SUB_TYPES_PATH );
        final Node moduleNode = JcrHelper.getOrAddNode( subTypesNode, qualifiedName.getModuleName().toString() );
        return JcrHelper.getOrAddNode( moduleNode, qualifiedName.getLocalName() );
    }

}
