package com.enonic.wem.web.data;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.api.Client;
import com.enonic.wem.web.rpc.WebRpcHandler;

public abstract class AbstractDataRpcHandler
    extends WebRpcHandler
{
    protected Client client;

    public AbstractDataRpcHandler( final String name )
    {
        super( name );
    }

    @Autowired
    public final void setClient( final Client client )
    {
        this.client = client;
    }
}
