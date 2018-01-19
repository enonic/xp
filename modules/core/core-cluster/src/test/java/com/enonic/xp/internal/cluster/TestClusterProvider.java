package com.enonic.xp.internal.cluster;

import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderHealth;
import com.enonic.xp.cluster.ClusterProviderId;

class TestClusterProvider
    implements ClusterProvider
{
    private ClusterProviderId id;

    private ClusterProviderHealth health;

    private ClusterNodes nodes;

    private boolean active = false;

    private TestClusterProvider( final Builder builder )
    {
        id = builder.id;
        health = builder.health;
        nodes = builder.nodes;
    }

    boolean isActive()
    {
        return active;
    }

    static Builder create()
    {
        return new Builder();
    }

    void setHealth( final ClusterProviderHealth health )
    {
        this.health = health;
    }

    void setNodes( final ClusterNodes nodes )
    {
        this.nodes = nodes;
    }

    @Override
    public ClusterProviderId getId()
    {
        return this.id;
    }

    @Override
    public ClusterProviderHealth getHealth()
    {
        return this.health;
    }

    @Override
    public ClusterNodes getNodes()
    {
        return this.nodes;
    }

    @Override
    public void activate()
    {
        if ( !active )
        {
            this.active = true;
        }
    }

    @Override
    public void deactivate()
    {
        if ( active )
        {
            this.active = false;
        }
    }

    public static final class Builder
    {
        private ClusterProviderId id;

        private ClusterProviderHealth health;

        private ClusterNodes nodes;

        private Builder()
        {
        }

        public Builder id( final ClusterProviderId val )
        {
            id = val;
            return this;
        }

        public Builder health( final ClusterProviderHealth val )
        {
            health = val;
            return this;
        }

        public Builder nodes( final ClusterNodes val )
        {
            nodes = val;
            return this;
        }

        public TestClusterProvider build()
        {
            return new TestClusterProvider( this );
        }
    }
}
