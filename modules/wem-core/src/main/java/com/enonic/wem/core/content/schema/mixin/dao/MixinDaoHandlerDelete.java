package com.enonic.wem.core.content.schema.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;


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
