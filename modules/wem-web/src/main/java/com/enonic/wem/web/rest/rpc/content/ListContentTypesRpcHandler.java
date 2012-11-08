package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
