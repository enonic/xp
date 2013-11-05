package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.core.command.CommandHandler;


public final class DeleteMixinHandler
    extends CommandHandler<DeleteMixin>
{
    @Override
    public void handle()
        throws Exception
    {
        final MixinName mixinName = command.getName();

        final DeleteNodeByPath deleteNodeByPathCommand =
            Commands.node().delete().byPath( new NodePath( "/mixins/" + mixinName.toString() ) );

        final DeleteNodeResult result = context.getClient().execute( deleteNodeByPathCommand );

        switch ( result )
        {
            case SUCCESS:
                command.setResult( DeleteMixinResult.SUCCESS );
                break;
            case NOT_FOUND:
                command.setResult( DeleteMixinResult.NOT_FOUND );
                break;
            default:
                command.setResult( DeleteMixinResult.UNABLE_TO_DELETE );
        }
    }
}
