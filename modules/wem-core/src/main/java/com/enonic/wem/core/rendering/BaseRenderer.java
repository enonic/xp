package com.enonic.wem.core.rendering;


import com.enonic.wem.api.Client;

public abstract class BaseRenderer
{
    protected final Client client;

    protected final Context context;

    protected BaseRenderer( final Client client, final Context context )
    {
        this.client = client;
        this.context = context;
    }

}
