package com.enonic.wem.admin.rest.rpc.schema.content;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.content.ContentTypes;


public final class ListContentTypesRpcHandler
    extends AbstractDataRpcHandler
{
    public ListContentTypesRpcHandler()
    {
        super( "contentType_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final ContentTypes contentTypes = client.execute( Commands.contentType().get().all() );

        context.setResult( new ListContentTypesRpcJsonResult( contentTypes ) );
    }

}
