package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;


public class ElasticsearchInstance
{
    private final ElasticsearchServer elasticsearchServer;

    private final Path snapshotsDir;

    public ElasticsearchInstance( final Path rootPath )
        throws IOException
    {
        if ( !Files.isDirectory( rootPath ) )
        {
            throw new IllegalArgumentException( "rootPath must be directory" );
        }

        final Path dataDir = rootPath.resolve( ElasticsearchConstants.ROOT_DATA_DIR_NAME );

        final Path esTmpDir = rootPath.resolve( ElasticsearchConstants.ELASTICSEARCH_TMP_DIR_NAME );
        Files.createDirectories( esTmpDir );

        final Path pathData = dataDir.resolve( "data" );
        Files.createDirectories( pathData );

        final Path pathLogs = dataDir.resolve( "logs" );
        Files.createDirectories( pathData );

        snapshotsDir = dataDir.resolve( "repo" );
        Files.createDirectories( snapshotsDir );

        final Path esConfigDir = dataDir.resolve( "config" );
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
            writer.write( "discovery.type: single-node" );
            writer.newLine();
        }

        elasticsearchServer = ElasticsearchServer.ElasticsearchServerBuilder.builder().
            esPathConf( esConfigDir ).
            esPathTmp( esTmpDir ).
            downloaderConfig( ElasticsearchDownloaderConfig.builder().build() ).
            build();
    }

    public void start()
        throws IOException
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
