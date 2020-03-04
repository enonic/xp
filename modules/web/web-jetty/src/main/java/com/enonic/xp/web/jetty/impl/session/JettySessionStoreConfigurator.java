package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;

public interface JettySessionStoreConfigurator
{
    void configure( Server server );
}
