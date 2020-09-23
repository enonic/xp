package com.enonic.xp.web.jetty.impl.configurator;

import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.web.jetty.impl.JettyConfig;

import static org.mockito.Mockito.mock;

public abstract class JettyConfiguratorTest<T>
{
    protected JettyConfig config;

    private JettyConfigurator<T> configurator;

    protected T object;

    @BeforeEach
    public final void setup()
    {
        this.config = mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
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
