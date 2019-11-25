package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchConfig;
import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ROOT_DATA_DIR;
import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.TMP_ELASTICSEARCH_DIR;

public class ElasticsearchInstance
{

    private ElasticsearchServer elasticsearchServer;

    private Path snapshotsDir;

    public ElasticsearchInstance()
    {
        initialize();

        elasticsearchServer = ElasticsearchServer.ElasticsearchServerBuilder.builder().
            esJavaOpts( "-Xms512m -Xmx512m" ).
            downloaderConfig( ElasticsearchDownloaderConfig.builder().
                build() ).
            elasticsearchConfig( ElasticsearchConfig.builder().
                setting( "path.repo", Collections.singletonList( snapshotsDir.toAbsolutePath().toString() ) ).
                build() ).
            build();
    }

    public void initialize()
    {
        try
        {
            Path rootDirectory = Files.createTempDirectory( TMP_ELASTICSEARCH_DIR );

            final Path dataDir = rootDirectory.resolve( ROOT_DATA_DIR );

            final Path pathHome = dataDir.resolve( "index" );
            Files.createDirectories( pathHome );

            final Path pathData = dataDir.resolve( "data" );
            Files.createDirectories( pathData );

            snapshotsDir = dataDir.resolve( "repo" );
            Files.createDirectories( snapshotsDir );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
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
