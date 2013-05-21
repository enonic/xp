package com.enonic.wem.web.rest.rpc.schema.mixin;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


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
        final QualifiedMixinNames qualifiedMixinNames =
            QualifiedMixinNames.from( context.param( "qualifiedMixinNames" ).required().asStringArray() );

        final MixinDeletionResult deletionResult = new MixinDeletionResult();
        for ( QualifiedMixinName qualifiedMixinName : qualifiedMixinNames )
        {
            final DeleteMixin deleteMixin = Commands.mixin().delete().name( qualifiedMixinName );
            final DeleteMixinResult result = client.execute( deleteMixin );
            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( qualifiedMixinName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( qualifiedMixinName,
                                            String.format( "Mixin [%s] was not found", qualifiedMixinName.toString() ) );
                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( qualifiedMixinName,
                                            String.format( "Unable to delete Mixin [%s]", qualifiedMixinName.toString() ) );
                    break;
            }
        }

        context.setResult( new DeleteMixinJsonResult( deletionResult ) );
    }
}
