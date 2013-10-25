package com.enonic.wem.core.hazelcast;

import java.io.InputStream;

import javax.inject.Provider;

import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;

@Singleton
public final class HazelcastConfigurator
    implements Provider<Config>
{
    private final Config config;

    public HazelcastConfigurator()
    {
        final InputStream in = getClass().getResourceAsStream( "hazelcast.xml" );
        final XmlConfigBuilder configBuilder = new XmlConfigBuilder( in );

        // Customize some configuration here if necessary

        this.config = configBuilder.build();
    }

    @Override
    public Config get()
    {
        return this.config;
    }
}
