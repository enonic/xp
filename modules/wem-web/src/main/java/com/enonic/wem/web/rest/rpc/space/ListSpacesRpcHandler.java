package com.enonic.wem.web.rest.rpc.space;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


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
