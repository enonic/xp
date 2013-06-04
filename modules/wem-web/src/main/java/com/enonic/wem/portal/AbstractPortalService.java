package com.enonic.wem.portal;

import javax.inject.Inject;

import com.enonic.wem.api.Client;

public class AbstractPortalService
{
    protected Client client;

    @Inject
    public final void setClient( final Client client )
    {
        this.client = client;
    }


}
