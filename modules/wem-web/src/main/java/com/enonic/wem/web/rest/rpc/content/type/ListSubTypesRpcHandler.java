package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class ListSubTypesRpcHandler
    extends AbstractDataRpcHandler
{
    public ListSubTypesRpcHandler()
    {
        super( "subType_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final SubTypes subTypes = client.execute( Commands.subType().get().all() );

        context.setResult( new ListSubTypesRpcJsonResult( subTypes ) );
    }
}
