package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ElasticsearchClusterReporter
    implements StatusReporter
{
    private final ClusterStateProvider clusterStateProvider;

    private final ClusterHealthProvider clusterHealthProvider;

    @Activate
    public ElasticsearchClusterReporter( @Reference final ClusterStateProvider clusterStateProvider,
                                         @Reference final ClusterHealthProvider clusterHealthProvider )
    {
        this.clusterStateProvider = clusterStateProvider;
        this.clusterHealthProvider = clusterHealthProvider;
    }

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
}
