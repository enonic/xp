package com.enonic.wem.core.content.type.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.core.jcr.JcrHelper;

abstract class AbstractMixinDaoHandler
{
    protected final Session session;

    protected final MixinJcrMapper mixinJcrMapper = new MixinJcrMapper();

    AbstractMixinDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getMixinNode( final QualifiedMixinName qualifiedName )
        throws RepositoryException
    {
        final String path = getNodePath( qualifiedName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final QualifiedMixinName qualifiedName )
    {
        return MixinDao.MIXINS_PATH + qualifiedName.getModuleName() + "/" + qualifiedName.getLocalName();
    }

    protected final boolean mixinExists( final QualifiedMixinName qualifiedName )
        throws RepositoryException
    {
        final String mixinPath = getNodePath( qualifiedName );
        return session.getRootNode().hasNode( mixinPath );
    }

}
