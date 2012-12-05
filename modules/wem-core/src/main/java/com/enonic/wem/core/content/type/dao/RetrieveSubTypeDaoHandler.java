package com.enonic.wem.core.content.type.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.jcr.JcrHelper;


final class RetrieveSubTypeDaoHandler
    extends AbstractSubTypeDaoHandler
{
    RetrieveSubTypeDaoHandler( final Session session )
    {
        super( session );
    }

    SubTypes retrieve( final QualifiedSubTypeNames qualifiedContentTypeNames )
        throws RepositoryException
    {
        final List<SubType> subTypeList = Lists.newArrayList();
        for ( QualifiedSubTypeName qualifiedName : qualifiedContentTypeNames )
        {
            final SubType subType = retrieveSubType( qualifiedName );
            if ( subType != null )
            {
                subTypeList.add( subType );
            }
        }
        return SubTypes.from( subTypeList );
    }

    private SubType retrieveSubType( final QualifiedSubTypeName qualifiedName )
        throws RepositoryException
    {
        final Node subTypeNode = getSubTypeNode( qualifiedName );
        if ( subTypeNode == null )
        {
            return null;
        }

        return subTypeJcrMapper.toSubType( subTypeNode );
    }

    public SubTypes retrieveAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node subTypesNode = JcrHelper.getNodeOrNull( rootNode, SubTypeDao.SUB_TYPES_PATH );

        final List<SubType> subTypeList = Lists.newArrayList();
        final NodeIterator subTypeModuleNodes = subTypesNode.getNodes();
        while ( subTypeModuleNodes.hasNext() )
        {
            final Node subTypeModuleNode = subTypeModuleNodes.nextNode();

            final NodeIterator subTypeNodes = subTypeModuleNode.getNodes();
            while ( subTypeNodes.hasNext() )
            {
                final Node subTypeNode = subTypeNodes.nextNode();
                final SubType subType = this.subTypeJcrMapper.toSubType( subTypeNode );
                subTypeList.add( subType );
            }
        }

        return SubTypes.from( subTypeList );
    }
}
