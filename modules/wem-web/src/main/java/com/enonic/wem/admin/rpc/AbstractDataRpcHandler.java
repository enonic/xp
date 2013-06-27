package com.enonic.wem.admin.rpc;

import javax.inject.Inject;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.api.Client;

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
