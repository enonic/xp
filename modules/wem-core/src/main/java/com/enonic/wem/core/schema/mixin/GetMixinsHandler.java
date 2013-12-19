package com.enonic.wem.core.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.command.CommandHandler;


public final class GetMixinsHandler
    extends CommandHandler<GetMixins>
{
    private final static MixinNodeTranslator MIXIN_NODE_TRANSLATOR = new MixinNodeTranslator();

    private final List<MixinName> noFoundList = new ArrayList<>();

    @Override
    public void handle()
        throws Exception
    {
        final MixinNames mixinNames = command.getNames();
        final Mixins mixins;

        if ( command.isGetAll() )
        {
            mixins = getAllMixins();

            if ( mixins.isEmpty() )
            {
                throw new MixinNotFoundException( mixinNames );
            }
        }
        else
        {
            mixins = getMixins( mixinNames );

            if ( !noFoundList.isEmpty() )
            {
                throw new MixinNotFoundException( MixinNames.from( noFoundList ) );
            }
        }

        command.setResult( mixins );
    }

    private Mixins getAllMixins()
    {
        final Nodes nodes = context.getClient().execute( Commands.node().get().byParent( new NodePath( "/mixins" ) ) );
        return MIXIN_NODE_TRANSLATOR.fromNodes( nodes );
    }

    private Mixins getMixins( final MixinNames mixinNames )
    {
        final List<Mixin> mixinList = new ArrayList<>();

        for ( MixinName mixinName : mixinNames )
        {
            final Mixin mixin = getMixin( mixinName );
            if ( mixin != null )
            {
                mixinList.add( mixin );
            }
            else
            {
                noFoundList.add( mixinName );
            }
        }
        return Mixins.from( mixinList );
    }

    private Mixin getMixin( final MixinName mixinName )
    {
        final NodePath nodePath = new NodePath( "/mixins/" + mixinName.toString() );
        final GetNodeByPath getNodeByPathCommand = Commands.node().get().byPath( nodePath );

        final Node node = context.getClient().execute( getNodeByPathCommand );

        return ( node != null ) ? MIXIN_NODE_TRANSLATOR.fromNode( node ) : null;
    }
}
