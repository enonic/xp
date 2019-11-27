package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ROOT_DATA_DIR;
import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.TMP_ELASTICSEARCH_DIR;

public class ElasticsearchInstance
{

    private ElasticsearchServer elasticsearchServer;

    private Path snapshotsDir;

    private Path esConfigDir;

    public ElasticsearchInstance()
    {
        initialize();

        elasticsearchServer = ElasticsearchServer.ElasticsearchServerBuilder.builder().
            esPathConf( esConfigDir.toAbsolutePath().toString() ).
            downloaderConfig( ElasticsearchDownloaderConfig.builder().build() ).
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

            esConfigDir = dataDir.resolve( "elasticConfig" );
            Files.createDirectories( esConfigDir );

            copyResource( "jvm.options" );
            copyResource( "log4j2.properties" );

            final Path elasticsearchYml = esConfigDir.resolve( "elasticsearch.yml" );
            Files.createFile( elasticsearchYml );

            final String builder = "path.repo: [" + snapshotsDir.toString() + "]";
            Files.writeString( elasticsearchYml, builder );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
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

    private void copyResource( final String resourceName )
        throws Exception
    {
        URL resource = getClass().getClassLoader().getResource( "elastic/config/" + resourceName );

        if ( resource != null )
        {
            Files.copy( Paths.get( resource.toURI() ), esConfigDir.resolve( resourceName ) );
        }
    }

}
