package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
