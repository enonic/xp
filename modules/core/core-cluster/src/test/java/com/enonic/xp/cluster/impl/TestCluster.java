package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;

public class TestCluster
    implements Cluster
{
    private final ClusterId id;

    private ClusterHealth health;

    private ClusterNodes nodes;

    private boolean active = false;

    private TestCluster( final Builder builder )
    {
        id = builder.id;
        health = builder.health;
        nodes = builder.nodes;
    }

    @Override
    public boolean isEnabled()
    {
        return active;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void setHealth( final ClusterHealth health )
    {
        this.health = health;
    }

    public void setNodes( final ClusterNodes nodes )
    {
        this.nodes = nodes;
    }

    @Override
    public ClusterId getId()
    {
        return this.id;
    }

    @Override
    public ClusterHealth getHealth()
    {
        return this.health;
    }

    @Override
    public ClusterNodes getNodes()
    {
        return this.nodes;
    }

    @Override
    public void enable()
    {
        if ( !active )
        {
            this.active = true;
        }
    }

    @Override
    public void disable()
    {
        if ( active )
        {
            this.active = false;
        }
    }

    public static final class Builder
    {
        private ClusterId id;

        private ClusterHealth health;

        private ClusterNodes nodes;

        private Builder()
        {
        }

        public Builder id( final ClusterId val )
        {
            id = val;
            return this;
        }

        public Builder health( final ClusterHealth val )
        {
            health = val;
            return this;
        }

        public Builder nodes( final ClusterNodes val )
        {
            nodes = val;
            return this;
        }

        public TestCluster build()
        {
            return new TestCluster( this );
        }
    }
}
