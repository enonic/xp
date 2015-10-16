package com.enonic.xp.web.jetty.impl.configurator;

import com.enonic.xp.web.jetty.impl.JettyConfig;

public abstract class JettyConfigurator<T>
{
    protected JettyConfig config;

    protected T object;

    public final void configure( final JettyConfig config, final T object )
    {
        this.config = config;
        this.object = object;
        doConfigure();
    }

    protected abstract void doConfigure();
}
