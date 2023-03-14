package com.enonic.xp.repo.impl.elasticsearch;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class EmbeddedElasticsearchServer
{
    private static final String ROOT_DATA_DIRECTORY = "elasticsearch-data";

    private final Node node;

    private static final Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    private final Path snaphotsDir;

    public EmbeddedElasticsearchServer( final Path rootDirectory )
    {
        LOG.info( " --- Starting ES integration test server instance" );

        System.setProperty( "mapper.allow_dots_in_name", "true" );

        final Path pathHome;
        final Path pathData;
        try
        {
            Path dataDirectory = rootDirectory.resolve( ROOT_DATA_DIRECTORY );
            Files.createDirectory( dataDirectory );
            pathHome = dataDirectory.resolve( "index" );
            Files.createDirectory( pathHome );
            pathData = dataDirectory.resolve( "data" );
            Files.createDirectory( pathData );
            snaphotsDir = dataDirectory.resolve( "repo" );
            Files.createDirectory( snaphotsDir );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        long now = System.currentTimeMillis();
        Settings.Builder testServerSetup = Settings.settingsBuilder()
            .put( "name", "repo-node-" + now )
            .put( "node.local", true )
            .put( "action.auto_create_index", false )
            .put( "path.data", pathData.toString() )
            .put( "path.home", pathHome.toString() )
            .put( "path.repo", this.snaphotsDir.toString() )
            .put( "cluster.name", "repo-test-cluster-" + now )
            .put( "http.enabled", false )
            .put( "index.translog.durability", "async" )
            .put( "index.translog.sync_interval", "15m" )
            .put( "index.search.slowlog.threshold.query.trace", "0s")
            .put( "index.search.slowlog.threshold.fetch.trace", "0s")
            .put( "discovery.zen.ping.multicast.enabled", false );

        node = nodeBuilder().
            local( true ).
            settings( testServerSetup.build() ).
            node();
    }

    public Client getClient()
    {
        return node.client();
    }

    public void shutdown()
    {
        LOG.info( " --- Shutting down ES integration test server instance" );
        node.close();
    }

    public Path getSnapshotsDir()
    {
        return this.snaphotsDir;
    }
}
