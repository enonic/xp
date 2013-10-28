package com.enonic.wem.core.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.command.CommandHandler;


public final class GetMixinsHandler
    extends CommandHandler<GetMixins>
{
    private final static MixinNodeTranslator MIXIN_NODE_TRANSLATOR = new MixinNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Mixins mixins;

        if ( command.isGetAll() )
        {
            mixins = getAllMixins();
        }
        else
        {
            final MixinNames qualifiedNames = command.getMixinNames();
            mixins = getMixins( qualifiedNames );
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
        final List<Mixin> mixinList = new ArrayList<>( mixinNames.getSize() );
        for ( MixinName qualifiedName : mixinNames )
        {
            final Mixin mixin = getMixin( qualifiedName );
            if ( mixin != null )
            {
                mixinList.add( mixin );
            }
        }
        return Mixins.from( mixinList );
    }

    private Mixin getMixin( final MixinName qualifiedName )
    {
        try
        {
            final NodePath nodePath = new NodePath( "/mixins/" + qualifiedName.toString() );
            final GetNodeByPath getNodeByPathCommand = Commands.node().get().byPath( nodePath );

            context.getClient().execute( getNodeByPathCommand );
            final Node node = getNodeByPathCommand.getResult();
            return MIXIN_NODE_TRANSLATOR.fromNode( node );
        }
        catch ( NoNodeAtPathFound e )
        {
            // TODO: swallow for now...
            return null;
        }
    }
}
