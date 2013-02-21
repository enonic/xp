package com.enonic.wem.web.rest.rpc;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

public abstract class AbstractDataRpcHandler
    extends JsonRpcHandler
{
    protected Client client;

    public AbstractDataRpcHandler( final String name )
    {
        super( name );
    }

    @Inject
    public final void setClient( final Client client )
    {
        this.client = client;
    }
}
