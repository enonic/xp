package com.enonic.wem.core.content.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.exception.SystemException;


final class MixinDaoHandlerUpdate
    extends AbstractMixinDaoHandler
{
    MixinDaoHandlerUpdate( final Session session )
    {
        super( session );
    }

    void update( final Mixin mixin )
        throws RepositoryException
    {
        final QualifiedMixinName qualifiedName = mixin.getQualifiedName();
        final Node node = getMixinNode( qualifiedName );
        if ( node == null )
        {
            throw new SystemException( "Mixin not found: {0}", qualifiedName.toString() );
        }

        this.mixinJcrMapper.toJcr( mixin, node );
    }

}
