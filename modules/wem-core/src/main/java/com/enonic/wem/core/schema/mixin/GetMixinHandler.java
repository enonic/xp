package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.command.schema.mixin.GetMixin;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandHandler;


public final class GetMixinHandler
    extends CommandHandler<GetMixin>
{
    private final static MixinNodeTranslator MIXIN_NODE_TRANSLATOR = new MixinNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final NodePath nodePath = new NodePath( "/mixins/" + command.getName().toString() );
        final GetNodeByPath getNodeByPathCommand = Commands.node().get().byPath( nodePath );

        final Node node = context.getClient().execute( getNodeByPathCommand );

        if ( node != null )
        {
            final Mixin mixin = MIXIN_NODE_TRANSLATOR.fromNode( node );
            command.setResult( mixin );
        }
        else
        {
            command.setResult( null );
        }
    }
}
