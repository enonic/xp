package com.enonic.wem.core.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;

import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.NodeDao;
import com.enonic.wem.core.item.dao.NodeJcrDao;


public final class GetMixinsHandler
    extends CommandHandler<GetMixins>
{
    private final static MixinItemTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinItemTranslator();

    private NodeDao nodeDao;

    @VisibleForTesting
    public void setNodeJcrDao( NodeJcrDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    @Override
    public void handle()
        throws Exception
    {
        if ( this.nodeDao == null )
        {
            nodeDao = new NodeJcrDao( context.getJcrSession() );
        }

        final Mixins mixins;

        if ( command.isGetAll() )
        {
            mixins = getAllMixins( nodeDao );
        }
        else
        {
            final QualifiedMixinNames qualifiedNames = command.getQualifiedMixinNames();
            mixins = getMixins( qualifiedNames, nodeDao );
        }

        command.setResult( mixins );
    }

    private Mixins getAllMixins( final NodeDao nodeDao )
    {
        final List<Node> nodes = nodeDao.getNodesByParentPath( new NodePath( "/mixins" ) );
        final List<Mixin> mixinList = new ArrayList<>( nodes.size() );
        for ( Node node : nodes )
        {
            mixinList.add( MIXIN_TO_ITEM_TRANSLATOR.fromItem( node ) );
        }
        return Mixins.from( mixinList );
    }

    private Mixins getMixins( final QualifiedMixinNames qualifiedMixinNames, final NodeDao nodeDao )
    {
        final List<Mixin> mixinList = new ArrayList<>( qualifiedMixinNames.getSize() );
        for ( QualifiedMixinName qualifiedName : qualifiedMixinNames )
        {
            final Mixin mixin = getMixin( qualifiedName, nodeDao );
            if ( mixin != null )
            {
                mixinList.add( mixin );
            }
        }
        return Mixins.from( mixinList );
    }

    private Mixin getMixin( final QualifiedMixinName qualifiedName, final NodeDao nodeDao )
    {
        try
        {
            final Node node = nodeDao.getNodeByPath( new NodePath( "/mixins/" + qualifiedName.toString() ) );
            return MIXIN_TO_ITEM_TRANSLATOR.fromItem( node );
        }
        catch ( NoNodeAtPathFound e )
        {
            // TODO: swallow for now...
            return null;
        }
    }
}
