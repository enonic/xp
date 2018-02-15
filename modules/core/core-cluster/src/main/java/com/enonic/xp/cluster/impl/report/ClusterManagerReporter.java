package com.enonic.xp.cluster.impl.report;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class ClusterManagerReporter
    extends JsonStatusReporter
{
    private ClusterManager clusterManager;

    @Override
    public JsonNode getReport()
    {
        return ClusterManagerReport.create().
            clusters( clusterManager.getClusters() ).
            clusterState( clusterManager.getClusterState() ).
            build().
            toJson();
    }

    @Override
    public String getName()
    {
        return "cluster.manager";
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setClusterManager( final ClusterManager clusterManager )
    {
        this.clusterManager = clusterManager;
    }
}
