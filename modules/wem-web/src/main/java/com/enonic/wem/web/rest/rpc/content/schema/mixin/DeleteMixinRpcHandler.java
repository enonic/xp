package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.mixin.DeleteMixins;
import com.enonic.wem.api.content.schema.mixin.MixinDeletionResult;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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

        final DeleteMixins deleteMixins = Commands.mixin().delete().names( qualifiedMixinNames );
        final MixinDeletionResult deletionResult = client.execute( deleteMixins );
        context.setResult( new DeleteMixinJsonResult( deletionResult ) );
    }
}
