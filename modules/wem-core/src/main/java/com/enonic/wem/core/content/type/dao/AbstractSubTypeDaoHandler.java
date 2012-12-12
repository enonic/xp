package com.enonic.wem.core.content.type.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.core.jcr.JcrHelper;

abstract class AbstractSubTypeDaoHandler
{
    protected final Session session;

    protected final SubTypeJcrMapper subTypeJcrMapper = new SubTypeJcrMapper();

    AbstractSubTypeDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getSubTypeNode( final QualifiedSubTypeName qualifiedName )
        throws RepositoryException
    {
        final String path = getNodePath( qualifiedName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final QualifiedSubTypeName qualifiedName )
    {
        return SubTypeDao.SUB_TYPES_PATH + qualifiedName.getModuleName() + "/" + qualifiedName.getLocalName();
    }

    protected final boolean subTypeExists( final QualifiedSubTypeName qualifiedName )
        throws RepositoryException
    {
        final String subTypePath = getNodePath( qualifiedName );
        return session.getRootNode().hasNode( subTypePath );
    }

}
