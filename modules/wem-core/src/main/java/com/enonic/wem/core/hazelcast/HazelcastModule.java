package com.enonic.wem.core.hazelcast;

import com.google.inject.AbstractModule;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;

public final class HazelcastModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( Config.class ).toProvider( HazelcastConfigurator.class );
        bind( HazelcastInstance.class ).toProvider( HazelcastProvider.class );
    }
}
