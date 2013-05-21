package com.enonic.wem.core.schema.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;


final class MixinDaoHandlerDelete
    extends AbstractMixinDaoHandler
{
    MixinDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    void handle( final QualifiedMixinName qualifiedMixinName )
        throws RepositoryException
    {
        final Node mixinNode = getMixinNode( qualifiedMixinName );

        if ( mixinNode == null )
        {
            throw new MixinNotFoundException( qualifiedMixinName );
        }

        mixinNode.remove();
    }
}
