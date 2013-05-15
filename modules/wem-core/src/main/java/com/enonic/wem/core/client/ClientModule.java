package com.enonic.wem.core.client;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.Client;

public final class ClientModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( Client.class ).to( StandardClient.class ).in( Scopes.SINGLETON );
    }
}
