package com.enonic.xp.ignite.impl.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.NodeDiscovery;

class TestDiscovery
    implements NodeDiscovery
{
    private final List<InetAddress> hosts = Lists.newArrayList();

    TestDiscovery( final String... hosts )
    {
        for ( final String host : hosts )
        {
            try
            {
                this.hosts.add( InetAddress.getByName( host ) );
            }
            catch ( UnknownHostException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    static TestDiscovery from( final String... hosts )
    {
        return new TestDiscovery( hosts );
    }

    @Override
    public List<InetAddress> get()
    {
        return this.hosts;
    }
}
