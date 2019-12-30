package com.enonic.xp.elasticsearch.server.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ElasticsearchServerSettings
{

    private final String pathData;

    private final String pathRepo;

    private final String pathWork;

    private final String pathLogs;

    private final String pathConf;

    private final String clusterName;

    private final boolean clusterRoutingAllocationDiskThresholdEnabled;

    private final String httpPort;

    private final String transportPort;

    private final int gatewayExpectedNodes;

    private final String gatewayRecoverAfterTime;

    private final int gatewayRecoverAfterNodes;

    private final int indexMaxResultWindow;

    private ElasticsearchServerSettings( final Builder builder )
    {
        this.pathData = builder.pathData;
        this.pathRepo = builder.pathRepo;
        this.pathWork = builder.pathWork;
        this.pathLogs = builder.pathLogs;
        this.pathConf = builder.pathConf;
        this.clusterName = builder.clusterName;
        this.clusterRoutingAllocationDiskThresholdEnabled = builder.clusterRoutingAllocationDiskThresholdEnabled;
        this.httpPort = builder.httpPort;
        this.transportPort = builder.transportPort;
        this.gatewayExpectedNodes = builder.gatewayExpectedNodes;
        this.gatewayRecoverAfterTime = builder.gatewayRecoverAfterTime;
        this.gatewayRecoverAfterNodes = builder.gatewayRecoverAfterNodes;
        this.indexMaxResultWindow = builder.indexMaxResultWindow;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public String getPathData()
    {
        return pathData;
    }

    public String getPathRepo()
    {
        return pathRepo;
    }

    public String getPathWork()
    {
        return pathWork;
    }

    public String getPathLogs()
    {
        return pathLogs;
    }

    public String getPathConf()
    {
        return pathConf;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public boolean getClusterRoutingAllocationDiskThresholdEnabled()
    {
        return clusterRoutingAllocationDiskThresholdEnabled;
    }

    public String getHttpPort()
    {
        return httpPort;
    }

    public String getTransportPort()
    {
        return transportPort;
    }

    public int getGatewayExpectedNodes()
    {
        return gatewayExpectedNodes;
    }

    public String getGatewayRecoverAfterTime()
    {
        return gatewayRecoverAfterTime;
    }

    public int getGatewayRecoverAfterNodes()
    {
        return gatewayRecoverAfterNodes;
    }

    public int getIndexMaxResultWindow()
    {
        return indexMaxResultWindow;
    }

    public void writeToYml( final Path elasticsearchYml )
    {
        try (final BufferedWriter writer = Files.newBufferedWriter( elasticsearchYml ))
        {
            writeProperty( "http.port", httpPort, writer );
            writeProperty( "path.data", pathData, writer );
            writeProperty( "path.repo", pathRepo, writer );
            writeProperty( "path.logs", pathLogs, writer );
            writeProperty( "cluster.name", clusterName, writer );
            writeProperty( "cluster.routing.allocation.disk.threshold_enabled", clusterRoutingAllocationDiskThresholdEnabled, writer );
            writeProperty( "transport.port", transportPort, writer );
            writeProperty( "gateway.expected_nodes", gatewayExpectedNodes, writer );
            writeProperty( "gateway.recover_after_time", gatewayRecoverAfterTime, writer );
            writeProperty( "gateway.recover_after_nodes", gatewayRecoverAfterNodes, writer );

            writeProperty( "discovery.type", "single-node", writer );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private void writeProperty( final String property, final Object value, final BufferedWriter writer )
        throws IOException
    {
        if ( value != null )
        {
            final Object normalizedValue = value instanceof String ? "'" + value + "'" : value;

            writer.write( property + ": " + normalizedValue );
            writer.newLine();
        }
    }

    public static class Builder
    {

        private String pathData;

        private String pathRepo;

        private String pathWork;

        private String pathLogs;

        private String pathConf;

        private String clusterName;

        private boolean clusterRoutingAllocationDiskThresholdEnabled;

        private String httpPort;

        private String transportPort;

        private int gatewayExpectedNodes;

        private String gatewayRecoverAfterTime;

        private int gatewayRecoverAfterNodes;

        private int indexMaxResultWindow;

        public Builder pathData( final String pathData )
        {
            this.pathData = pathData;
            return this;
        }

        public Builder pathRepo( final String pathRepo )
        {
            this.pathRepo = pathRepo;
            return this;
        }

        public Builder pathWork( final String pathWork )
        {
            this.pathWork = pathWork;
            return this;
        }

        public Builder pathLogs( final String pathLogs )
        {
            this.pathLogs = pathLogs;
            return this;
        }

        public Builder pathConf( final String pathConf )
        {
            this.pathConf = pathConf;
            return this;
        }

        public Builder clusterName( final String clusterName )
        {
            this.clusterName = clusterName;
            return this;
        }

        public Builder clusterRoutingAllocationDiskThresholdEnabled( final boolean clusterRoutingAllocationDiskThresholdEnabled )
        {
            this.clusterRoutingAllocationDiskThresholdEnabled = clusterRoutingAllocationDiskThresholdEnabled;
            return this;
        }

        public Builder httpPort( final String httpPort )
        {
            this.httpPort = httpPort;
            return this;
        }

        public Builder transportPort( final String transportPort )
        {
            this.transportPort = transportPort;
            return this;
        }

        public Builder gatewayExpectedNodes( final int gatewayExpectedNodes )
        {
            this.gatewayExpectedNodes = gatewayExpectedNodes;
            return this;
        }

        public Builder gatewayRecoverAfterTime( final String gatewayRecoverAfterTime )
        {
            this.gatewayRecoverAfterTime = gatewayRecoverAfterTime;
            return this;
        }

        public Builder gatewayRecoverAfterNodes( final int gatewayRecoverAfterNodes )
        {
            this.gatewayRecoverAfterNodes = gatewayRecoverAfterNodes;
            return this;
        }

        public Builder indexMaxResultWindow( final int indexMaxResultWindow )
        {
            this.indexMaxResultWindow = indexMaxResultWindow;
            return this;
        }

        public ElasticsearchServerSettings build()
        {
            return new ElasticsearchServerSettings( this );
        }

    }

}
