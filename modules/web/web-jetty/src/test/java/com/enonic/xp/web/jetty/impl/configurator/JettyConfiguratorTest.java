package com.enonic.xp.web.jetty.impl.configurator;

import org.junit.Before;

import com.enonic.xp.web.jetty.impl.JettyConfig;
import com.enonic.xp.web.jetty.impl.JettyConfigMockFactory;

public abstract class JettyConfiguratorTest<T>
{
    protected JettyConfig config;

    private JettyConfigurator<T> configurator;

    protected T object;

    @Before
    public final void setup()
    {
        this.config = new JettyConfigMockFactory().newConfig();
        this.configurator = newConfigurator();
        this.object = setupObject();
    }

    protected abstract T setupObject();

    protected abstract JettyConfigurator<T> newConfigurator();

    protected final void configure()
    {
        this.configurator.configure( this.config, this.object );
    }
}