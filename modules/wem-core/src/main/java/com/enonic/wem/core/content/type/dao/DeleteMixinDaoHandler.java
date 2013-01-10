package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;


final class DeleteMixinDaoHandler
    extends AbstractMixinDaoHandler
{
    DeleteMixinDaoHandler( final Session session )
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
