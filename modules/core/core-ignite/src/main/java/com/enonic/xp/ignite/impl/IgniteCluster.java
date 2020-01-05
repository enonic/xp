package com.enonic.xp.ignite.impl;

import org.apache.ignite.Ignite;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;

@Component(immediate = true)
public class IgniteCluster
    implements Cluster
{
    private final Ignite ignite;

    @Activate
    public IgniteCluster( @Reference final Ignite ignite )
    {
        this.ignite = ignite;
    }

    @Override
    public ClusterId getId()
    {
        return ClusterId.from( "ignite" );
    }

    @Override
    public ClusterHealth getHealth()
    {
        return ClusterHealth.green();
    }

    @Override
    public ClusterNodes getNodes()
    {
        try
        {
            return doGetNodes();
        }
        catch ( java.lang.IllegalStateException e )
        {
            return ClusterNodes.create().build();
        }
    }

    private ClusterNodes doGetNodes()
    {
        final ClusterNodes.Builder builder = ClusterNodes.create();

        this.ignite.cluster().nodes().forEach( node -> {

            node.addresses();

            builder.add( ClusterNode.from( node.consistentId().toString() ) );
        } );

        return builder.build();
    }

    @Override
    public void enable()
    {
        // Ignore. Ignite Cluster should be always enabled if Ignite exists
    }

    @Override
    public void disable()
    {
        // Ignore. Ignite Cluster should be always enabled if Ignite exists
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
