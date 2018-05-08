package com.enonic.xp.cluster.impl.discovery;

import java.net.InetAddress;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.NodeDiscovery;

public class StaticIpNodeDiscovery
    implements NodeDiscovery
{
    private final List<InetAddress> hosts;

    private StaticIpNodeDiscovery( final Builder builder )
    {
        hosts = builder.hosts;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public List<InetAddress> get()
    {
        return hosts;
    }

    public static final class Builder
    {
        private List<InetAddress> hosts = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final InetAddress val )
        {
            this.hosts.add( val );
            return this;
        }

        public Builder hosts( final List<InetAddress> val )
        {
            hosts = val;
            return this;
        }

        public StaticIpNodeDiscovery build()
        {
            return new StaticIpNodeDiscovery( this );
        }
    }
}
