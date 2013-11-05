package com.enonic.wem.admin.rpc.schema.mixin;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;


public class DeleteMixinRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteMixinRpcHandler()
    {
        super( "mixin_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final MixinNames mixinNames = MixinNames.from( context.param( "mixinNames" ).required().asStringArray() );

        final MixinDeletionResult deletionResult = new MixinDeletionResult();
        for ( MixinName mixinName : mixinNames )
        {
            final DeleteMixin deleteMixin = Commands.mixin().delete().name( mixinName );
            final DeleteMixinResult result = client.execute( deleteMixin );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( mixinName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( mixinName, String.format( "Mixin [%s] was not found", mixinName.toString() ) );
                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( mixinName, String.format( "Unable to delete Mixin [%s]", mixinName.toString() ) );
                    break;
            }
        }

        context.setResult( new DeleteMixinJsonResult( deletionResult ) );
    }
}
