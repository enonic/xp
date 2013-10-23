package com.enonic.wem.admin.rpc.space;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.space.Spaces;


public final class ListSpacesRpcHandler
    extends AbstractDataRpcHandler
{

    public ListSpacesRpcHandler()
    {
        super( "space_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Spaces spaces = client.execute( Commands.space().get().all() );
        context.setResult( new ListSpacesJsonResult( spaces ) );
    }

}
