package com.enonic.xp.web.jetty.impl.session;

import org.eclipse.jetty.server.Server;

public interface JettySessionStorageConfigurator
{
    void configure( Server server );
}
