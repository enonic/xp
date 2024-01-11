package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ElasticsearchClusterReporter
    implements StatusReporter
{
    private ClusterStateProvider clusterStateProvider;

    private ClusterHealthProvider clusterHealthProvider;

    @Override
    public String getName()
    {
        return "cluster.elasticsearch";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    JsonNode getReport()
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
