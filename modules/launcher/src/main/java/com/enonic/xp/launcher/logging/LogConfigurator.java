package com.enonic.xp.launcher.logging;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.enonic.xp.launcher.env.Environment;

public final class LogConfigurator
{
    private final Environment env;

    public LogConfigurator( final Environment env )
    {
        this.env = env;
    }

    public void configure()
    {
        SLF4JBridgeHandler.install();
    }
}
