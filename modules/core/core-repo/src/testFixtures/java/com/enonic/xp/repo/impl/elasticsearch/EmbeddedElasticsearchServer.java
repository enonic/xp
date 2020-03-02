package com.enonic.xp.repo.impl.elasticsearch;


import java.io.File;

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

    private final String dataDirectory;

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    private final long now = System.currentTimeMillis();

    private final File snaphotsDir;

    public EmbeddedElasticsearchServer( final File rootDirectory )
    {
        LOG.info( " --- Starting ES integration test server instance" );

        System.setProperty( "mapper.allow_dots_in_name", "true" );
        this.dataDirectory = new File( rootDirectory, ROOT_DATA_DIRECTORY ).toString();

        final File pathHome = new File( this.dataDirectory, "index" );
        pathHome.mkdir();
        final File pathData = new File( this.dataDirectory, "data" );
        pathData.mkdir();
        snaphotsDir = new File( this.dataDirectory, "repo" );
        snaphotsDir.mkdir();

        Settings.Builder testServerSetup = Settings.settingsBuilder().
            put( "name", "repo-node-" + this.now ).
            put( "client", "false" ).
            put( "data", "true" ).
            put( "local", "true" ).
            put( "path.data", pathData.getPath() ).
            put( "path.home", pathHome.getPath() ).
            put( "path.repo", this.snaphotsDir.getPath() ).
            put( "cluster.name", "repo-test-cluster-" + this.now ).
            put( "http.enabled", "false" ).
            put( "index.translog.durability", "async" ).
            put( "index.translog.sync_interval", "15m" ).
            put( "discovery.zen.ping.multicast.enabled", "false" );

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

    public File getSnapshotsDir()
    {
        return this.snaphotsDir;
    }
}
