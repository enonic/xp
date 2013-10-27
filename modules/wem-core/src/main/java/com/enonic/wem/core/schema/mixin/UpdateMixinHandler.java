package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.UpdateNode;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.entity.NodeEditor;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandHandler;


public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private final static MixinNodeTranslator MIXIN_TO_ITEM_TRANSLATOR = new MixinNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Mixin mixin = context.getClient().execute( Commands.mixin().get().byQualifiedName( command.getQualifiedName() ) );
        if ( mixin == null )
        {
            command.setResult( UpdateMixinResult.NOT_FOUND );
        }
        else
        {
            final Mixin modifiedMixin = command.getEditor().edit( mixin );
            if ( modifiedMixin != null )
            {
                final NodeEditor nodeEditor = MIXIN_TO_ITEM_TRANSLATOR.toItemEditor( modifiedMixin );
                UpdateNode updateNode = MIXIN_TO_ITEM_TRANSLATOR.toUpdateItemCommand( mixin.getId(), nodeEditor );
                context.getClient().execute( updateNode );
                command.setResult( UpdateMixinResult.SUCCESS );
            }
            command.setResult( UpdateMixinResult.SUCCESS );
        }
    }
}
