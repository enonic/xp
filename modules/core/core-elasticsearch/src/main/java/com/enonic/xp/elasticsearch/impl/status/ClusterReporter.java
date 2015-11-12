package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class ClusterReporter
    implements StatusReporter
{
    private ClusterStateProvider clusterStateProvider;

    private ClusterHealthProvider clusterHealthProvider;

    @Override
    public String getName()
    {
        return "cluster";
    }

    @Override
    public ObjectNode getReport()
    {
        final ClusterReport clusterReport = ClusterReport.create().
            clusterState( clusterStateProvider.getInfo() ).
            clusterHealth( clusterHealthProvider.getInfo() ).
            build();

        return clusterReport.toJson();
    }

    @Reference
    public void setClient( final Client client )
    {
        this.clusterStateProvider = new ClusterStateProvider( client );
        this.clusterHealthProvider = new ClusterHealthProvider( client );
    }
}
