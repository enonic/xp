package com.enonic.wem.web.rest.rpc.userstore;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


public final class GetUserstoreConnectorsRpcHandler
    extends AbstractDataRpcHandler
{
    public GetUserstoreConnectorsRpcHandler()
    {
        super( "userstore_getConnectors" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final UserStoreConnectors connectors = this.client.execute( Commands.userStore().getConnectors() );
        context.setResult( new GetUserstoreConnectorsJsonResult( connectors ) );
    }
}
