package com.enonic.wem.itests.core.elasticsearch;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class EmbeddedElasticsearchServer
{

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;

    private final String dataDirectory;

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    public EmbeddedElasticsearchServer()
    {
        this( DEFAULT_DATA_DIRECTORY );
    }

    public EmbeddedElasticsearchServer( String dataDirectory )
    {
        LOG.info( " --- Starting ES integration test server instance" );

        this.dataDirectory = dataDirectory;

        ImmutableSettings.Builder testServerSetup = ImmutableSettings.settingsBuilder().
            put( "name", "integration-test-node-2" ).
            put( "client", "false" ).
            put( "data", "true" ).
            put( "local", "true" ).
            put( "http.enabled", "false" ).
            put( "path.data", dataDirectory ).
            put( "gateway.type", "none" ).
            put( "cluster.name", "integration-test-cluster" ).
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
        deleteDataDirectory();
    }

    private void deleteDataDirectory()
    {
        LOG.info( "Deleting index data directories" );

        try
        {
            FileUtils.deleteDirectory( new File( dataDirectory ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Could not delete data directory of embedded elasticsearch server", e );
        }
    }

}
