package com.enonic.wem.web.data.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.rest2.resource.account.graph.GraphResource;
import com.enonic.wem.web.rpc.JsonRpcContext;

@Component
public final class GetGraphRpcHandler
    extends AbstractDataRpcHandler
{
    @Autowired
    private GraphResource resource;

    public GetGraphRpcHandler()
    {
        super( "account_getGraph" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();

        final JsonSerializable json = this.resource.getInfo( key );
        context.setResult( json );
    }
}
