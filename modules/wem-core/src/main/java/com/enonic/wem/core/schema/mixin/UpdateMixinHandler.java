package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.command.CommandHandler;


public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private final static MixinNodeTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Mixin mixin = context.getClient().execute( Commands.mixin().get().byName( command.getName() ) );
        if ( mixin == null )
        {
            throw new MixinNotFoundException( command.getName() );
        }

        final Mixin modifiedMixin = command.getEditor().edit( mixin );
        if ( modifiedMixin != null )
        {
            final NodeEditor nodeEditor = MIXIN_TO_ITEM_TRANSLATOR.toNodeEditor( modifiedMixin );

            UpdateNode updateNode = MIXIN_TO_ITEM_TRANSLATOR.toUpdateNodeCommand( mixin.getId(), nodeEditor );

            final UpdateNodeResult updateNodeResult = context.getClient().execute( updateNode );
            final Mixin updatedMixin = MIXIN_TO_ITEM_TRANSLATOR.fromNode( updateNodeResult.getPersistedNode() );
            command.setResult( new UpdateMixinResult( updatedMixin ) );
        }
        else
        {
            command.setResult( new UpdateMixinResult( mixin ) );
        }
    }
}
