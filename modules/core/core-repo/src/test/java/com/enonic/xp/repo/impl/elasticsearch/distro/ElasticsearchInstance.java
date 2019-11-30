package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;


public class ElasticsearchInstance
{

    private ElasticsearchServer elasticsearchServer;

    private Path snapshotsDir;

    private Path esConfigDir;

    private Path esTmpDir;

    public ElasticsearchInstance()
        throws IOException
    {
        initialize();

        elasticsearchServer = ElasticsearchServer.ElasticsearchServerBuilder.builder().
            esPathConf( esConfigDir ).
            esPathTmp( esTmpDir ).
            downloaderConfig( ElasticsearchDownloaderConfig.builder().build() ).
            build();
    }

    private void initialize()
        throws IOException
    {
        Path rootDirectory = Files.createTempDirectory( ElasticsearchConstants.FIXTURE_ELASTICSEARCH_DIR );

        final Path dataDir = rootDirectory.resolve( ElasticsearchConstants.ROOT_DATA_DIR_NAME );

        esTmpDir = rootDirectory.resolve( ElasticsearchConstants.ELASTICSEARCH_TMP_DIR_NAME );
        Files.createDirectories( esTmpDir );

        final Path pathHome = dataDir.resolve( "index" );
        Files.createDirectories( pathHome );

        final Path pathData = dataDir.resolve( "data" );
        Files.createDirectories( pathData );

        final Path pathLogs = dataDir.resolve( "logs" );
        Files.createDirectories( pathData );

        snapshotsDir = dataDir.resolve( "repo" );
        Files.createDirectories( snapshotsDir );

        esConfigDir = dataDir.resolve( "config" );
        Files.createDirectories( esConfigDir );

        final Path elasticsearchYml = esConfigDir.resolve( "elasticsearch.yml" );

        try (final BufferedWriter writer = Files.newBufferedWriter( elasticsearchYml ))
        {
            writer.write( "path.repo: '" + snapshotsDir + "'" );
            writer.newLine();
            writer.write( "path.logs: '" + pathLogs + "'" );
            writer.newLine();
            writer.write( "path.data: '" + pathData + "'" );
            writer.newLine();
        }
    }

    public void start()
        throws IOException, InterruptedException
    {
        if ( !elasticsearchServer.isStarted() )
        {
            elasticsearchServer.start();
        }
    }

    public void stop()
    {
        if ( elasticsearchServer.isStarted() )
        {
            elasticsearchServer.stop();
        }
    }

    public Path getSnapshotsDir()
    {
        return snapshotsDir;
    }
}
