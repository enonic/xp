package com.enonic.wem.core.content.type.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.core.jcr.JcrHelper;


final class RetrieveMixinDaoHandler
    extends AbstractMixinDaoHandler
{
    RetrieveMixinDaoHandler( final Session session )
    {
        super( session );
    }

    Mixins retrieve( final QualifiedMixinNames qualifiedContentTypeNames )
        throws RepositoryException
    {
        final List<Mixin> mixinList = Lists.newArrayList();
        for ( QualifiedMixinName qualifiedName : qualifiedContentTypeNames )
        {
            final Mixin mixin = retrieveMixin( qualifiedName );
            if ( mixin != null )
            {
                mixinList.add( mixin );
            }
        }
        return Mixins.from( mixinList );
    }

    private Mixin retrieveMixin( final QualifiedMixinName qualifiedName )
        throws RepositoryException
    {
        final Node node = getMixinNode( qualifiedName );
        if ( node == null )
        {
            return null;
        }

        return mixinJcrMapper.toMixin( node );
    }

    public Mixins retrieveAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node mixinsNode = JcrHelper.getNodeOrNull( rootNode, MixinDao.MIXINS_PATH );

        final List<Mixin> mixinList = Lists.newArrayList();
        final NodeIterator mixinModuleNodes = mixinsNode.getNodes();
        while ( mixinModuleNodes.hasNext() )
        {
            final Node mixinModuleNode = mixinModuleNodes.nextNode();

            final NodeIterator mixinNodes = mixinModuleNode.getNodes();
            while ( mixinNodes.hasNext() )
            {
                final Node mixinNode = mixinNodes.nextNode();
                final Mixin mixin = this.mixinJcrMapper.toMixin( mixinNode );
                mixinList.add( mixin );
            }
        }

        return Mixins.from( mixinList );
    }
}
