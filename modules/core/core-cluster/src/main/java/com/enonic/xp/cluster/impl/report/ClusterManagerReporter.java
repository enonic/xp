package com.enonic.xp.cluster.impl.report;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class ClusterManagerReporter
    implements StatusReporter
{
    private final ClusterManager clusterManager;

    @Activate
    public ClusterManagerReporter( @Reference final ClusterManager clusterManager )
    {
        this.clusterManager = clusterManager;
    }

    @Override
    public final MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public final void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public String getName()
    {
        return "cluster.manager";
    }

    private JsonNode getReport()
    {
        return ClusterManagerReport.create().
            clusters( clusterManager.getClusters() ).
            clusterState( clusterManager.getClusterState() ).
            build().
            toJson();
    }
}
