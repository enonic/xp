package com.enonic.xp.elasticsearch.impl.status.cluster;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ElasticsearchClusterReporter
    extends JsonStatusReporter
{
    private ClusterStateProvider clusterStateProvider;

    private ClusterHealthProvider clusterHealthProvider;

    @Override
    public String getName()
    {
        return "cluster.elasticsearch";
    }

    @Override
    public JsonNode getReport()
    {
        final ElasticsearchClusterReport elasticsearchClusterReport = ElasticsearchClusterReport.create().
            clusterState( clusterStateProvider.getInfo() ).
            clusterHealth( clusterHealthProvider.getInfo() ).
            build();

        return elasticsearchClusterReport.toJson();
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setClusterStateProvider( final ClusterStateProvider clusterStateProvider )
    {
        this.clusterStateProvider = clusterStateProvider;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setClusterHealthProvider( final ClusterHealthProvider clusterHealthProvider )
    {
        this.clusterHealthProvider = clusterHealthProvider;
    }
}
