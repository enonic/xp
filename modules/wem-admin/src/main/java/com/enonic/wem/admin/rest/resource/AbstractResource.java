package com.enonic.wem.admin.rest.resource;

import javax.inject.Inject;

import com.enonic.wem.api.Client;

public abstract class AbstractResource
{
    protected Client client;

    @Inject
    public final void setClient( final Client client )
    {
        this.client = client;
    }
}
