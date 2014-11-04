package com.enonic.wem.itests.core.elasticsearch;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class EmbeddedElasticsearchServer
{

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;

    private final String dataDirectory;

    public EmbeddedElasticsearchServer()
    {
        this( DEFAULT_DATA_DIRECTORY );
    }

    public EmbeddedElasticsearchServer( String dataDirectory )
    {
        this.dataDirectory = dataDirectory;

        ImmutableSettings.Builder testSettings = ImmutableSettings.settingsBuilder().
            put( "http.enabled", "false" ).
            put( "path.data", dataDirectory ).
            put( "gateway.type", "none" );

        node = nodeBuilder().
            local( true ).
            settings( testSettings.build() ).
            node();
    }

    public Client getClient()
    {
        return node.client();
    }

    public void shutdown()
    {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory()
    {
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
