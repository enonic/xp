package com.enonic.wem.web.rest.rpc.content.type;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.BaseTypes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class ListBaseTypesRpcHandler
    extends AbstractDataRpcHandler
{
    public ListBaseTypesRpcHandler()
    {
        super( "baseType_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final BaseTypes baseTypes = client.execute( Commands.baseType().get() );

        context.setResult( new ListBaseTypesRpcJsonResult( baseTypes ) );
    }

}
