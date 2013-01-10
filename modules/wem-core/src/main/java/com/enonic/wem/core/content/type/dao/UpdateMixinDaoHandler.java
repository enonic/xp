package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.SystemException;


final class UpdateMixinDaoHandler
    extends AbstractMixinDaoHandler
{
    UpdateMixinDaoHandler( final Session session )
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
