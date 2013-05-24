package com.enonic.wem.admin.rest.rpc.schema.mixin;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.mixin.Mixins;


public class ListMixinsRpcHandler
    extends AbstractDataRpcHandler
{
    public ListMixinsRpcHandler()
    {
        super( "mixin_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Mixins mixins = client.execute( Commands.mixin().get().all() );

        context.setResult( new ListMixinsRpcJsonResult( mixins ) );
    }
}
