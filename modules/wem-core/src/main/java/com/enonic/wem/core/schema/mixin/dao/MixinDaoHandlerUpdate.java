package com.enonic.wem.core.schema.mixin.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;


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
