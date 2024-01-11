package com.enonic.xp.cluster.impl.report;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ClusterManagerReporter
    implements StatusReporter
{
    private ClusterManager clusterManager;

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
