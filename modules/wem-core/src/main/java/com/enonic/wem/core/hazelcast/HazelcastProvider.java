package com.enonic.wem.core.hazelcast;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public final class HazelcastProvider
    extends LifecycleBean
    implements Provider<HazelcastInstance>
{
    private final HazelcastInstance instance;

    @Inject
    public HazelcastProvider( final Config config )
    {
        super( RunLevel.L1 );
        this.instance = Hazelcast.newHazelcastInstance( config );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        // Do nothing
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.instance.shutdown();
    }

    @Override
    public HazelcastInstance get()
    {
        return this.instance;
    }
}
